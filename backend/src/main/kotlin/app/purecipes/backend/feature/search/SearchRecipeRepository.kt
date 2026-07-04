package app.purecipes.backend.feature.search

import app.purecipes.backend.feature.recipe.RecipeRepository
import app.purecipes.backend.feature.recipe.RecipeRepositorySql
import app.purecipes.backend.feature.recipe.countRecipesByKeyword
import app.purecipes.backend.feature.recipe.countSearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.isRecipeCoveredByAvailableIngredients
import app.purecipes.backend.feature.recipe.querySearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.recipeContainsAllKeyIngredients
import app.purecipes.backend.feature.recipe.recipeContainsExcludedIngredient
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchRequest
import app.purecipes.shared.domain.model.SearchResultsPage
import java.sql.Connection
import javax.sql.DataSource

class SearchRecipeRepository(
	private val dataSource: DataSource,
) {

	private val recipeRepository = RecipeRepository(dataSource)

	fun searchByKeywordPaginated(
		keyword: String,
		pageNumber: Int = 1,
		pageSize: Int = 20,
	): SearchResultsPage {
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		val searchInput =
			prepareKeywordSearchInput(keyword, pageNumber, normalizedPageSize) ?: return SearchResultsPage(
				items = emptyList(),
				pageNumber = 1,
				pageSize = normalizedPageSize,
				totalMatches = 0,
			)
		val items = searchByKeyword(searchInput)
		val totalMatches = countRecipesByKeyword(dataSource, searchInput.like)
		return SearchResultsPage(
			items = items,
			pageNumber = searchInput.pageNumber,
			pageSize = searchInput.pageSize,
			totalMatches = totalMatches,
		)
	}

	fun searchWithFilters(request: SearchRequest, userId: Long? = null): SearchResultsPage {
		val normalizedPageNumber = request.pageNumber.coerceAtLeast(1)
		val normalizedPageSize = request.pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		val (whereClause, params) = buildSearchWhereClause(request)

		val (items, totalMatches) = dataSource.connection.use { conn ->
			searchWithFiltersOnConnection(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = normalizedPageSize,
				offset = offset,
				userId = userId,
				keyIngredients = request.keyIngredients.map { it.trim() }.filter { it.isNotEmpty() }.distinct(),
			)
		}

		return SearchResultsPage(items, normalizedPageNumber, normalizedPageSize, totalMatches)
	}

	private fun buildSearchWhereClause(request: SearchRequest): Pair<String, List<Any>> {
		val conditions = mutableListOf<String>()
		val params = mutableListOf<Any>()
		if (request.query.isNotBlank()) {
			val like = "%${request.query.trim().lowercase()}%"
			conditions.add("(LOWER(r.title) LIKE ? OR LOWER(r.cuisine) LIKE ?)")
			params.add(like)
			params.add(like)
		}
		addCuisineFilterConditions(request.filters, conditions, params)
		addCookingTimeFilterConditions(request.filters, conditions)
		addEnrichmentFilterConditions(request.filters, conditions, params)
		val whereClause = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"
		return whereClause to params
	}

	private fun searchWithFiltersOnConnection(
		conn: Connection,
		whereClause: String,
		params: List<Any>,
		limit: Int,
		offset: Int,
		userId: Long?,
		keyIngredients: List<String>,
	): Pair<List<RecipeSummary>, Int> {
		val availableIngredients =
			if (userId != null) loadAvailableIngredientsForUser(conn, userId) else emptyList()
		val excludedIngredients =
			if (userId != null) loadExcludedIngredientsForUser(conn, userId) else emptyList()
		val requiresIngredientPostFilter =
			availableIngredients.isNotEmpty() || excludedIngredients.isNotEmpty() || keyIngredients.isNotEmpty()
		return if (!requiresIngredientPostFilter) {
			val total = countSearchWithFiltersRecipes(conn, whereClause, params)
			val page = querySearchWithFiltersRecipes(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = limit,
				offset = offset,
				executeQuery = recipeRepository::executeQuery,
			)
			page to total
		} else {
			val filtered = querySearchWithFiltersRecipes(
				conn = conn,
				whereClause = whereClause,
				params = params,
				executeQuery = recipeRepository::executeQuery,
			).filter { summary ->
				matchesIngredientFilters(
					conn = conn,
					summary = summary,
					availableIngredients = availableIngredients,
					excludedIngredients = excludedIngredients,
					keyIngredients = keyIngredients,
				)
			}
			filtered.drop(offset).take(limit) to filtered.size
		}
	}

	private fun matchesIngredientFilters(
		conn: Connection,
		summary: RecipeSummary,
		availableIngredients: List<String>,
		excludedIngredients: List<String>,
		keyIngredients: List<String>,
	): Boolean {
		val loadIngredientGroups = { recipeId: Int ->
			recipeRepository.loadIngredientGroupsForRecipe(conn, recipeId)
		}
		val containsExcluded = recipeContainsExcludedIngredient(
			recipeId = summary.id,
			excludedIngredients = excludedIngredients,
			loadIngredientGroups = loadIngredientGroups,
		)
		val containsAllKeyIngredients = recipeContainsAllKeyIngredients(
			recipeId = summary.id,
			keyIngredients = keyIngredients,
			loadIngredientGroups = loadIngredientGroups,
		)
		val coveredByPantry = availableIngredients.isEmpty() ||
			isRecipeCoveredByAvailableIngredients(
				recipeId = summary.id,
				availableIngredients = availableIngredients,
				loadIngredientGroups = loadIngredientGroups,
			)
		return !containsExcluded && containsAllKeyIngredients && coveredByPantry
	}

	private fun searchByKeyword(searchInput: KeywordSearchInput): List<RecipeSummary> {
		val sql = """
			SELECT id, title, cuisine, image_url, total_time, measurement_system
			FROM recipes
			WHERE LOWER(title) LIKE ? OR LOWER(cuisine) LIKE ?
			ORDER BY created_at DESC
			LIMIT ? OFFSET ?
		""".trimIndent()
		return recipeRepository.searchRecipes(sql, searchInput.like, searchInput.pageSize, searchInput.offset)
	}

	private fun prepareKeywordSearchInput(keyword: String, pageNumber: Int, pageSize: Int): KeywordSearchInput? {
		val trimmed = keyword.trim()
		if (trimmed.isEmpty()) {
			return null
		}
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val offset = (normalizedPageNumber - 1) * pageSize
		return KeywordSearchInput(
			like = "%${trimmed.lowercase()}%",
			pageNumber = normalizedPageNumber,
			pageSize = pageSize,
			offset = offset,
		)
	}
}

private fun loadAvailableIngredientsForUser(conn: Connection, userId: Long): List<String> {
	return conn.prepareStatement(RecipeRepositorySql.GET_USER_PANTRY_SQL).use { ps ->
		ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
		ps.executeQuery().use { rs ->
			buildList {
				while (rs.next()) {
					val ingredient = rs.getString("ingredient")?.trim().orEmpty()
					if (ingredient.isNotEmpty()) {
						add(ingredient)
					}
				}
			}.distinct()
		}
	}
}

private fun loadExcludedIngredientsForUser(conn: Connection, userId: Long): List<String> {
	return conn.prepareStatement(RecipeRepositorySql.GET_USER_EXCLUDED_INGREDIENTS_SQL).use { ps ->
		ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
		ps.executeQuery().use { rs ->
			buildList {
				while (rs.next()) {
					val ingredient = rs.getString("ingredient")?.trim().orEmpty()
					if (ingredient.isNotEmpty()) {
						add(ingredient)
					}
				}
			}.distinct()
		}
	}
}

private data class KeywordSearchInput(
	val like: String,
	val pageNumber: Int,
	val pageSize: Int,
	val offset: Int,
)

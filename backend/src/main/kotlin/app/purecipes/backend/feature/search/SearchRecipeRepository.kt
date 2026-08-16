package app.purecipes.backend.feature.search

import app.purecipes.backend.feature.recipe.PantryCoverage
import app.purecipes.backend.feature.recipe.RecipeRepository
import app.purecipes.backend.feature.recipe.RecipeRepositorySql
import app.purecipes.backend.feature.recipe.countRecipesByKeyword
import app.purecipes.backend.feature.recipe.countSearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.isRecipeCoveredByAvailableIngredients
import app.purecipes.backend.feature.recipe.pantryCoverageForRecipe
import app.purecipes.backend.feature.recipe.querySearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.recipeContainsAllKeyIngredients
import app.purecipes.backend.feature.recipe.recipeContainsExcludedIngredient
import app.purecipes.backend.feature.recipe.singleMissingPantryIngredientLabel
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
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
		val isTitleSearch = request.query.isNotBlank()
		val effectiveRequest = effectiveRequest(request, isTitleSearch)
		val (whereClause, params) = buildSearchWhereClause(effectiveRequest)

		val result = dataSource.connection.use { conn ->
			searchWithFiltersOnConnection(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = normalizedPageSize,
				offset = offset,
				userId = userId,
				keyIngredients = effectiveRequest.keyIngredients
					.map { it.trim() }
					.filter { it.isNotEmpty() }
					.distinct(),
				rankByPantryCoverage = isTitleSearch,
			)
		}

		return SearchResultsPage(
			items = result.items,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = result.totalMatches,
			nearMissRecipes = result.nearMissRecipes,
		)
	}

	private fun effectiveRequest(request: SearchRequest, isTitleSearch: Boolean): SearchRequest {
		return if (isTitleSearch && !request.applyRecipeFilters) {
			request.copy(filters = SearchFilters(), keyIngredients = emptySet())
		} else {
			request
		}
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
		rankByPantryCoverage: Boolean,
	): FilteredSearchResult {
		val availableIngredients =
			if (userId != null) loadAvailableIngredientsForUser(conn, userId) else emptyList()
		val excludedIngredients =
			if (userId != null) loadExcludedIngredientsForUser(conn, userId) else emptyList()
		val requiresIngredientPostFilter =
			availableIngredients.isNotEmpty() || excludedIngredients.isNotEmpty() || keyIngredients.isNotEmpty()
		val result = if (!requiresIngredientPostFilter) {
			val total = countSearchWithFiltersRecipes(conn, whereClause, params)
			val page = querySearchWithFiltersRecipes(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = limit,
				offset = offset,
				executeQuery = recipeRepository::executeQuery,
			)
			FilteredSearchResult(
				items = page,
				totalMatches = total,
				nearMissRecipes = emptyList(),
			)
		} else {
			searchWithIngredientPostFilter(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = limit,
				offset = offset,
				availableIngredients = availableIngredients,
				excludedIngredients = excludedIngredients,
				keyIngredients = keyIngredients,
				rankByPantryCoverage = rankByPantryCoverage,
			)
		}
		return markFavoriteRecipes(conn, userId, result)
	}

	private fun searchWithIngredientPostFilter(
		conn: Connection,
		whereClause: String,
		params: List<Any>,
		limit: Int,
		offset: Int,
		availableIngredients: List<String>,
		excludedIngredients: List<String>,
		keyIngredients: List<String>,
		rankByPantryCoverage: Boolean,
	): FilteredSearchResult {
		val candidates = querySearchWithFiltersRecipes(
			conn = conn,
			whereClause = whereClause,
			params = params,
			executeQuery = recipeRepository::executeQuery,
		)
		val loadIngredientGroups = { recipeId: Int ->
			recipeRepository.loadIngredientGroupsForRecipe(conn, recipeId)
		}
		val eligible = candidates.filter { summary ->
			passesExclusionAndKeyIngredientFilters(
				summary = summary,
				excludedIngredients = excludedIngredients,
				keyIngredients = keyIngredients,
				loadIngredientGroups = loadIngredientGroups,
			)
		}
		val matches = if (!rankByPantryCoverage && availableIngredients.isNotEmpty()) {
			eligible.filter { summary ->
				isRecipeCoveredByAvailableIngredients(
					recipeId = summary.id,
					availableIngredients = availableIngredients,
					loadIngredientGroups = loadIngredientGroups,
				)
			}
		} else {
			eligible
		}
		val orderedMatches = if (rankByPantryCoverage && availableIngredients.isNotEmpty()) {
			rankRecipesByPantryCoverage(
				recipes = matches,
				availableIngredients = availableIngredients,
				loadIngredientGroups = loadIngredientGroups,
			)
		} else {
			matches
		}
		return FilteredSearchResult(
			items = orderedMatches.drop(offset).take(limit),
			totalMatches = orderedMatches.size,
			nearMissRecipes = nearMissRecipesFor(
				eligible = eligible,
				orderedMatches = orderedMatches,
				availableIngredients = availableIngredients,
				rankByPantryCoverage = rankByPantryCoverage,
				offset = offset,
				loadIngredientGroups = loadIngredientGroups,
			),
		)
	}

	private fun nearMissRecipesFor(
		eligible: List<RecipeSummary>,
		orderedMatches: List<RecipeSummary>,
		availableIngredients: List<String>,
		rankByPantryCoverage: Boolean,
		offset: Int,
		loadIngredientGroups: (Int) -> List<IngredientGroup>,
	): List<NearMissRecipe> {
		val skipNearMiss = rankByPantryCoverage || availableIngredients.isEmpty()
		if (skipNearMiss || offset != 0 || orderedMatches.size >= NEAR_MISS_MATCH_THRESHOLD) {
			return emptyList()
		}
		val matchedRecipeIds = orderedMatches.map { summary -> summary.id }.toSet()
		return eligible
			.filter { summary -> summary.id !in matchedRecipeIds }
			.mapNotNull { summary ->
				singleMissingPantryIngredientLabel(
					recipeId = summary.id,
					availableIngredients = availableIngredients,
					loadIngredientGroups = loadIngredientGroups,
				)?.let { missingIngredient ->
					NearMissRecipe(
						recipe = summary,
						missingIngredient = missingIngredient,
					)
				}
			}
			.sortedWith(
				compareBy(
					{ nearMiss -> nearMiss.missingIngredient },
					{ nearMiss -> nearMiss.recipe.title },
				),
			)
			.take(MAX_NEAR_MISS_RECIPES)
	}

	private fun rankRecipesByPantryCoverage(
		recipes: List<RecipeSummary>,
		availableIngredients: List<String>,
		loadIngredientGroups: (Int) -> List<IngredientGroup>,
	): List<RecipeSummary> {
		return recipes
			.map { summary ->
				summary to pantryCoverageForRecipe(
					recipeId = summary.id,
					availableIngredients = availableIngredients,
					loadIngredientGroups = loadIngredientGroups,
				)
			}
			.sortedWith(pantryCoverageComparator)
			.map { ranked -> ranked.first }
	}

	private fun markFavoriteRecipes(
		conn: Connection,
		userId: Long?,
		result: FilteredSearchResult,
	): FilteredSearchResult {
		if (userId == null) {
			return result
		}
		val recipeIds = result.items.map { summary -> summary.id } +
			result.nearMissRecipes.map { nearMiss -> nearMiss.recipe.id }
		val favoriteIds = recipeRepository.loadFavoriteRecipeIds(conn, userId, recipeIds)
		return if (favoriteIds.isEmpty()) {
			result
		} else {
			result.copy(
				items = result.items.map { summary ->
					if (summary.id in favoriteIds) summary.copy(isFavorite = true) else summary
				},
				nearMissRecipes = result.nearMissRecipes.map { nearMiss ->
					if (nearMiss.recipe.id in favoriteIds) {
						nearMiss.copy(recipe = nearMiss.recipe.copy(isFavorite = true))
					} else {
						nearMiss
					}
				},
			)
		}
	}

	private fun passesExclusionAndKeyIngredientFilters(
		summary: RecipeSummary,
		excludedIngredients: List<String>,
		keyIngredients: List<String>,
		loadIngredientGroups: (Int) -> List<IngredientGroup>,
	): Boolean {
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
		return !containsExcluded && containsAllKeyIngredients
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

private const val NEAR_MISS_MATCH_THRESHOLD = 10
private const val MAX_NEAR_MISS_RECIPES = 30

private val pantryCoverageComparator: Comparator<Pair<RecipeSummary, PantryCoverage>> =
	compareBy<Pair<RecipeSummary, PantryCoverage>> { ranked -> ranked.second.missingSlots }
		.thenComparator { left, right ->
			val leftRatio = left.second.coveredSlots.toLong() * right.second.totalRequiredSlots
			val rightRatio = right.second.coveredSlots.toLong() * left.second.totalRequiredSlots
			rightRatio.compareTo(leftRatio)
		}
		.thenBy { ranked -> ranked.first.title.lowercase() }

private data class FilteredSearchResult(
	val items: List<RecipeSummary>,
	val totalMatches: Int,
	val nearMissRecipes: List<NearMissRecipe>,
)

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

package app.purecipes.backend.feature.search

import app.purecipes.backend.feature.recipe.RecipeRepository
import app.purecipes.backend.feature.recipe.SearchRecipeOrder
import app.purecipes.backend.feature.recipe.SearchRecipeQuery
import app.purecipes.backend.feature.recipe.countSearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.querySearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.readSearchRecipeCandidates
import app.purecipes.backend.feature.recipe.recipeCompletenessScoreSql
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeCollectionSource
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.ResultSet

internal class HomeCollectionRepository(
	private val recipeRepository: RecipeRepository,
) {

	fun liveShelves(
		connection: Connection,
		userId: Long?,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): List<HomeShelf> {
		return loadRows(connection, LIVE_COLLECTIONS_SQL, emptyList()).mapNotNull { row ->
			val recipes = loadRecipes(
				connection = connection,
				userId = userId,
				row = row,
				limit = HOME_SHELF_PAGE_SIZE,
				offset = 0,
				dietaryPreferences = dietaryPreferences,
			)
			if (recipes.isEmpty()) {
				null
			} else {
				shelf(row, recipes, dietaryPreferences)
			}
		}
	}

	fun page(
		connection: Connection,
		userId: Long?,
		slug: String,
		pageNumber: Int,
		pageSize: Int,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): SearchResultsPage? {
		val row = loadRows(connection, LIVE_COLLECTION_BY_SLUG_SQL, listOf(slug)).singleOrNull() ?: return null
		val offset = (pageNumber - 1) * pageSize
		val recipes = loadRecipes(
			connection = connection,
			userId = userId,
			row = row,
			limit = pageSize,
			offset = offset,
			dietaryPreferences = dietaryPreferences,
		)
		return SearchResultsPage(
			items = recipes,
			pageNumber = pageNumber,
			pageSize = pageSize,
			totalMatches = countRecipes(connection, row, dietaryPreferences),
		)
	}

	private fun loadRecipes(
		connection: Connection,
		userId: Long?,
		row: HomeCollectionRow,
		limit: Int,
		offset: Int,
		dietaryPreferences: Set<DietaryPreference>,
	): List<RecipeSummary> {
		val effectiveRow = if (dietaryPreferences.isEmpty()) {
			row
		} else {
			row.copy(
				filters = row.filters.copy(
					dietaryPreferences = row.filters.dietaryPreferences + dietaryPreferences,
				),
			)
		}
		val recipes = when (effectiveRow.source) {
			HomeCollectionSource.EDITORIAL -> editorialRecipes(
				connection = connection,
				collectionId = effectiveRow.id,
				limit = limit,
				offset = offset,
				dietaryPreferences = dietaryPreferences,
			)

			HomeCollectionSource.QUERY -> queryRecipes(connection, effectiveRow, limit, offset)
		}
		return markFavorites(connection, userId, recipes)
	}

	private fun countRecipes(
		connection: Connection,
		row: HomeCollectionRow,
		dietaryPreferences: Set<DietaryPreference>,
	): Int {
		val effectiveRow = if (dietaryPreferences.isEmpty()) {
			row
		} else {
			row.copy(
				filters = row.filters.copy(
					dietaryPreferences = row.filters.dietaryPreferences + dietaryPreferences,
				),
			)
		}
		return when (effectiveRow.source) {
			HomeCollectionSource.EDITORIAL -> countEditorialRecipes(
				connection = connection,
				collectionId = effectiveRow.id,
				dietaryPreferences = dietaryPreferences,
			)

			HomeCollectionSource.QUERY -> countSearchWithFiltersRecipes(
				connection,
				effectiveRow.query.whereClause,
				effectiveRow.query.params,
			)
		}
	}

	private fun editorialRecipes(
		connection: Connection,
		collectionId: Int,
		limit: Int,
		offset: Int,
		dietaryPreferences: Set<DietaryPreference>,
	): List<RecipeSummary> {
		val dietaryClauses = dietaryPreferences.map { "? = ANY(r.dietary_preferences)" }
		val extraWhere = if (dietaryClauses.isEmpty()) "" else " AND " + dietaryClauses.joinToString(" AND ")
		val sql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system, r.is_private,
				${recipeCompletenessScoreSql("r")} AS completeness_score,
				r.created_at
			FROM home_collection_recipes hcr
			INNER JOIN recipes r ON r.id = hcr.recipe_id
			WHERE hcr.collection_id = ?
				AND r.is_private = FALSE
				$extraWhere
			ORDER BY hcr.position ASC, r.id ASC
			LIMIT ? OFFSET ?
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setInt(paramIndex++, collectionId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
			statement.setInt(paramIndex++, limit)
			statement.setInt(paramIndex++, offset)
			recipeRepository.readSearchRecipeCandidates(statement).map { candidate -> candidate.summary }
		}
	}

	private fun queryRecipes(
		connection: Connection,
		row: HomeCollectionRow,
		limit: Int,
		offset: Int,
	): List<RecipeSummary> {
		return recipeRepository.querySearchWithFiltersRecipes(
			conn = connection,
			query = SearchRecipeQuery(
				whereClause = row.query.whereClause,
				params = row.query.params,
				order = row.sort,
				limit = limit,
				offset = offset,
			),
		).map { candidate -> candidate.summary }
	}

	private fun countEditorialRecipes(
		connection: Connection,
		collectionId: Int,
		dietaryPreferences: Set<DietaryPreference>,
	): Int {
		val dietaryClauses = dietaryPreferences.map { "? = ANY(r.dietary_preferences)" }
		val extraWhere = if (dietaryClauses.isEmpty()) "" else " AND " + dietaryClauses.joinToString(" AND ")
		val sql = """
			SELECT COUNT(*)
			FROM home_collection_recipes hcr
			INNER JOIN recipes r ON r.id = hcr.recipe_id
			WHERE hcr.collection_id = ?
				AND r.is_private = FALSE
				$extraWhere
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setInt(paramIndex++, collectionId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.getInt(1) else 0
			}
		}
	}

	private fun loadRows(
		connection: Connection,
		sql: String,
		params: List<Any>,
	): List<HomeCollectionRow> {
		return connection.prepareStatement(sql).use { statement ->
			params.forEachIndexed { index, param ->
				statement.setObject(index + 1, param)
			}
			statement.executeQuery().use { resultSet ->
				readRows(resultSet)
			}
		}
	}

	private fun readRows(resultSet: ResultSet): List<HomeCollectionRow> {
		return buildList {
			while (resultSet.next()) {
				readRow(resultSet)?.let { row -> add(row) }
			}
		}
	}

	private fun readRow(resultSet: ResultSet): HomeCollectionRow? {
		val source = collectionSource(resultSet.getString("source")) ?: return null
		val filters = readFilters(resultSet.getString("filter_payload"))
		return if (source == HomeCollectionSource.QUERY && filters == null) {
			null
		} else {
			HomeCollectionRow(
				id = resultSet.getInt("id"),
				slug = resultSet.getString("slug"),
				title = resultSet.getString("title"),
				source = source,
				filters = filters ?: SearchFilters(),
				sort = collectionSort(resultSet.getString("sort")),
			)
		}
	}

	private fun markFavorites(
		connection: Connection,
		userId: Long?,
		recipes: List<RecipeSummary>,
	): List<RecipeSummary> {
		val favoriteIds = if (userId == null || recipes.isEmpty()) {
			emptySet()
		} else {
			recipeRepository.loadFavoriteRecipeIds(
				conn = connection,
				userId = userId,
				recipeIds = recipes.map { recipe -> recipe.id },
			)
		}
		return if (favoriteIds.isEmpty()) {
			recipes
		} else {
			recipes.map { recipe ->
				if (recipe.id in favoriteIds) recipe.copy(isFavorite = true) else recipe
			}
		}
	}

	private fun shelf(
		row: HomeCollectionRow,
		recipes: List<RecipeSummary>,
		dietaryPreferences: Set<DietaryPreference>,
	): HomeShelf {
		val baseFilters = if (row.source == HomeCollectionSource.QUERY) row.filters else SearchFilters()
		val filters = if (dietaryPreferences.isEmpty()) {
			baseFilters
		} else {
			baseFilters.copy(
				dietaryPreferences = baseFilters.dietaryPreferences + dietaryPreferences,
			)
		}
		return HomeShelf(
			id = HomeShelfId.COLLECTION,
			title = row.title,
			filters = filters,
			recipes = recipes,
			collectionSlug = row.slug,
			collectionSource = row.source,
		)
	}
}

private data class HomeCollectionRow(
	val id: Int,
	val slug: String,
	val title: String,
	val source: HomeCollectionSource,
	val filters: SearchFilters,
	val sort: SearchRecipeOrder,
) {

	val query: ShelfFilterQuery
		get() = searchFilterQuery(filters)
}

private data class ShelfFilterQuery(
	val whereClause: String,
	val params: List<Any>,
)

private fun searchFilterQuery(filters: SearchFilters): ShelfFilterQuery {
	val conditions = mutableListOf("r.is_private = FALSE")
	val params = mutableListOf<Any>()
	addCuisineFilterConditions(filters, conditions, params)
	addCookingTimeFilterConditions(filters, conditions)
	addEnrichmentFilterConditions(filters, conditions, params)
	return ShelfFilterQuery(
		whereClause = "WHERE ${conditions.joinToString(separator = " AND ")}",
		params = params,
	)
}

private fun collectionSource(raw: String?): HomeCollectionSource? {
	return when (raw?.trim()?.uppercase()) {
		"EDITORIAL" -> HomeCollectionSource.EDITORIAL
		"QUERY" -> HomeCollectionSource.QUERY
		else -> null
	}
}

private fun collectionSort(raw: String?): SearchRecipeOrder {
	return if (raw?.trim()?.equals("newest", ignoreCase = true) == true) {
		SearchRecipeOrder.NEWEST
	} else {
		SearchRecipeOrder.CATALOG
	}
}

private fun readFilters(payload: String?): SearchFilters? {
	if (payload.isNullOrBlank()) {
		return SearchFilters()
	}
	return try {
		collectionJson.decodeFromString(SearchFilters.serializer(), payload)
	} catch (_: SerializationException) {
		null
	}
}

private const val COLLECTION_ID_INDEX = 1

private const val LIMIT_INDEX = 2

private const val OFFSET_INDEX = 3

private val collectionJson = Json {
	ignoreUnknownKeys = true
	explicitNulls = false
}

private const val LIVE_WINDOW_SQL = """
	enabled = TRUE
	AND (starts_at IS NULL OR starts_at <= CURRENT_TIMESTAMP)
	AND (ends_at IS NULL OR ends_at > CURRENT_TIMESTAMP)
"""

private val LIVE_COLLECTIONS_SQL = """
	SELECT id, slug, title, source, filter_payload, sort
	FROM home_collections
	WHERE $LIVE_WINDOW_SQL
	ORDER BY position ASC, id ASC
""".trimIndent()

private val LIVE_COLLECTION_BY_SLUG_SQL = """
	SELECT id, slug, title, source, filter_payload, sort
	FROM home_collections
	WHERE slug = ?
		AND $LIVE_WINDOW_SQL
""".trimIndent()

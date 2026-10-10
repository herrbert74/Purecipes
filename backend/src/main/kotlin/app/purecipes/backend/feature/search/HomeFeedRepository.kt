package app.purecipes.backend.feature.search

import app.purecipes.backend.feature.featurerequests.FeatureRequestRepository
import app.purecipes.backend.feature.recipe.RecipeRepository
import app.purecipes.backend.feature.recipe.RecipeRepositorySql
import app.purecipes.backend.feature.recipe.SearchRecipeOrder
import app.purecipes.backend.feature.recipe.SearchRecipeQuery
import app.purecipes.backend.feature.recipe.countSearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.querySearchWithFiltersRecipes
import app.purecipes.backend.feature.recipe.readSearchRecipeCandidates
import app.purecipes.backend.feature.recipe.recipeCompletenessScoreSql
import app.purecipes.shared.domain.model.CookingTimeRange
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import java.sql.Connection
import javax.sql.DataSource

private const val QUICK_SHELF_MAX_MINUTES = 30

private const val QUICK_SHELF_MINIMUM_RECIPES = 4

private const val NEW_SHELF_TITLE = "New"

private const val QUICK_SHELF_TITLE = "Quick"

private const val FAVORITES_SHELF_TITLE = "Your favorites"

class HomeFeedRepository(
	private val dataSource: DataSource,
) {

	private val recipeRepository = RecipeRepository(dataSource)

	private val featureRequestRepository = FeatureRequestRepository(dataSource)

	fun loadHomeFeed(userId: Long?, localHour: Int): HomeFeed {
		val mealType = mealTypeForLocalHour(localHour)
		val shelves = dataSource.connection.use { connection ->
			buildShelves(connection, userId, mealType)
		}
		return HomeFeed(
			shelves = shelves,
			featureRequest = featureRequestRepository.findHomeFeatureRequest(userId),
		)
	}

	fun loadShelfPage(
		userId: Long?,
		localHour: Int,
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): SearchResultsPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		val window = PageWindow(
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
		)
		val mealType = mealTypeForLocalHour(localHour)
		return dataSource.connection.use { connection ->
			if (shelfId == HomeShelfId.FAVORITES) {
				favoriteShelfPage(
					connection = connection,
					userId = userId,
					window = window,
				)
			} else {
				publicShelfPage(
					connection = connection,
					userId = userId,
					mealType = mealType,
					shelfId = shelfId,
					window = window,
				)
			}
		}
	}

	private fun buildShelves(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
	): List<HomeShelf> {
		return buildList {
			val newest = queryPublicShelf(
				connection = connection,
				userId = userId,
				query = ShelfQuery(
					extraConditions = listOf(
						"NULLIF(BTRIM(r.image_url), '') IS NOT NULL",
						"""
							EXISTS (
								SELECT 1
								FROM instruction_steps new_steps
								WHERE new_steps.recipe_id = r.id
							)
						""".trimIndent(),
					),
					params = emptyList(),
					order = SearchRecipeOrder.NEWEST,
				),
			)
			if (newest.isNotEmpty()) {
				add(
					HomeShelf(
						id = HomeShelfId.NEW,
						title = NEW_SHELF_TITLE,
						recipes = newest,
					),
				)
			}
			val rightNow = queryMealShelf(connection, userId, mealType)
			if (rightNow.isNotEmpty()) {
				add(
					HomeShelf(
						id = HomeShelfId.RIGHT_NOW,
						title = mealType.displayName,
						filters = SearchFilters(mealTypes = setOf(mealType)),
						recipes = rightNow,
					),
				)
			}
			val quick = queryQuickShelf(connection, userId, mealType)
			if (quick != null) {
				add(quick)
			}
			if (userId != null) {
				val favorites = queryFavoriteShelf(connection, userId)
				if (favorites.isNotEmpty()) {
					add(
						HomeShelf(
							id = HomeShelfId.FAVORITES,
							title = FAVORITES_SHELF_TITLE,
							recipes = favorites,
						),
					)
				}
			}
		}
	}

	private fun queryMealShelf(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
	): List<RecipeSummary> {
		return queryPublicShelf(
			connection = connection,
			userId = userId,
			query = ShelfQuery(
				extraConditions = listOf("r.meal_type = ?"),
				params = listOf(mealType.name),
				order = SearchRecipeOrder.CATALOG,
			),
		)
	}

	private fun queryQuickShelf(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
	): HomeShelf? {
		val conditions = quickConditions()
		val params = listOf(mealType.name)
		val whereClause = publicWhereClause(conditions)
		val matchCount = countSearchWithFiltersRecipes(connection, whereClause, params)
		val recipes = if (matchCount < QUICK_SHELF_MINIMUM_RECIPES) {
			emptyList()
		} else {
			queryPublicShelf(
				connection = connection,
				userId = userId,
				query = ShelfQuery(
					extraConditions = conditions,
					params = params,
					order = SearchRecipeOrder.CATALOG,
				),
			)
		}
		return if (recipes.isEmpty()) {
			null
		} else {
			HomeShelf(
				id = HomeShelfId.QUICK,
				title = QUICK_SHELF_TITLE,
				filters = SearchFilters(
					mealTypes = setOf(mealType),
					cookingTimeRanges = setOf(CookingTimeRange.UNDER_30),
				),
				recipes = recipes,
			)
		}
	}

	private fun queryPublicShelf(
		connection: Connection,
		userId: Long?,
		query: ShelfQuery,
		limit: Int = HOME_SHELF_PAGE_SIZE,
		offset: Int = 0,
	): List<RecipeSummary> {
		val candidates = recipeRepository.querySearchWithFiltersRecipes(
			conn = connection,
			query = SearchRecipeQuery(
				whereClause = publicWhereClause(query.extraConditions),
				params = query.params,
				order = query.order,
				limit = limit,
				offset = offset,
			),
		)
		return markFavorites(
			connection = connection,
			userId = userId,
			recipes = candidates.map { candidate -> candidate.summary },
		)
	}

	private fun queryFavoriteShelf(
		connection: Connection,
		userId: Long,
		limit: Int = HOME_SHELF_PAGE_SIZE,
		offset: Int = 0,
	): List<RecipeSummary> {
		val sql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system, r.is_private,
				${recipeCompletenessScoreSql("r")} AS completeness_score,
				r.created_at
			FROM recipes r
			INNER JOIN favorites fav ON fav.recipe_id = r.id
			WHERE fav.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
			ORDER BY fav.created_at DESC, r.id DESC
			LIMIT ? OFFSET ?
		""".trimIndent()
		val recipes = connection.prepareStatement(sql).use { statement ->
			statement.setLong(1, userId)
			statement.setLong(2, userId)
			statement.setInt(3, limit)
			statement.setInt(4, offset)
			recipeRepository.readSearchRecipeCandidates(statement).map { candidate ->
				candidate.summary.copy(isFavorite = true)
			}
		}
		return recipes
	}

	private fun publicShelfPage(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
		shelfId: HomeShelfId,
		window: PageWindow,
	): SearchResultsPage {
		val query = publicShelfQuery(shelfId, mealType)
		val whereClause = publicWhereClause(query.extraConditions)
		val totalMatches = countSearchWithFiltersRecipes(connection, whereClause, query.params)
		val recipes = queryPublicShelf(
			connection = connection,
			userId = userId,
			query = query,
			limit = window.pageSize,
			offset = window.offset,
		)
		return SearchResultsPage(
			items = recipes,
			pageNumber = window.pageNumber,
			pageSize = window.pageSize,
			totalMatches = totalMatches,
		)
	}

	private fun favoriteShelfPage(
		connection: Connection,
		userId: Long?,
		window: PageWindow,
	): SearchResultsPage {
		val totalMatches = if (userId == null) 0 else countFavorites(connection, userId)
		val recipes = if (userId == null) {
			emptyList()
		} else {
			queryFavoriteShelf(
				connection = connection,
				userId = userId,
				limit = window.pageSize,
				offset = window.offset,
			)
		}
		return SearchResultsPage(
			items = recipes,
			pageNumber = window.pageNumber,
			pageSize = window.pageSize,
			totalMatches = totalMatches,
		)
	}

	private fun publicShelfQuery(shelfId: HomeShelfId, mealType: MealType): ShelfQuery {
		return when (shelfId) {
			HomeShelfId.NEW -> ShelfQuery(
				extraConditions = listOf(
					"NULLIF(BTRIM(r.image_url), '') IS NOT NULL",
					"""
						EXISTS (
							SELECT 1
							FROM instruction_steps new_steps
							WHERE new_steps.recipe_id = r.id
						)
					""".trimIndent(),
				),
				params = emptyList(),
				order = SearchRecipeOrder.NEWEST,
			)

			HomeShelfId.RIGHT_NOW -> ShelfQuery(
				extraConditions = listOf("r.meal_type = ?"),
				params = listOf(mealType.name),
				order = SearchRecipeOrder.CATALOG,
			)

			HomeShelfId.QUICK -> ShelfQuery(
				extraConditions = quickConditions(),
				params = listOf(mealType.name),
				order = SearchRecipeOrder.CATALOG,
			)

			HomeShelfId.FAVORITES -> ShelfQuery(
				extraConditions = emptyList(),
				params = emptyList(),
				order = SearchRecipeOrder.CATALOG,
			)
		}
	}

	private fun countFavorites(connection: Connection, userId: Long): Int {
		val sql = """
			SELECT COUNT(*)
			FROM recipes r
			INNER JOIN favorites fav ON fav.recipe_id = r.id
			WHERE fav.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			statement.setLong(1, userId)
			statement.setLong(2, userId)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.getInt(1) else 0
			}
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

	private fun quickConditions(): List<String> = listOf(
		"r.meal_type = ?",
		"r.total_time IS NOT NULL",
		"r.total_time <= $QUICK_SHELF_MAX_MINUTES",
	)

	private fun publicWhereClause(extraConditions: List<String>): String {
		val conditions = listOf("r.is_private = FALSE") + extraConditions
		return "WHERE ${conditions.joinToString(separator = " AND ")}"
	}
}

private data class ShelfQuery(
	val extraConditions: List<String>,
	val params: List<Any>,
	val order: SearchRecipeOrder,
)

private data class PageWindow(
	val pageNumber: Int,
	val pageSize: Int,
) {

	val offset: Int
		get() = (pageNumber - 1) * pageSize
}

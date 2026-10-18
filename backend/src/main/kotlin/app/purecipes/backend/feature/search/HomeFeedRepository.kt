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
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.RecipeActivityEvent
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.domain.model.cuisineFromRawValue
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.ResultSet
import javax.sql.DataSource

private const val QUICK_SHELF_MAX_MINUTES = 30

private const val QUICK_SHELF_MINIMUM_RECIPES = 4

private const val HABIT_SHELF_MINIMUM_FAVORITES = 3

private const val ACTIVITY_HABIT_MINIMUM_EVENTS = 3

private const val HABIT_SHELF_LIMIT = 2

private const val NEW_SHELF_TITLE = "New"

private const val QUICK_SHELF_TITLE = "Quick"

private const val FAVORITES_SHELF_TITLE = "Your favorites"

private const val RECENTLY_VIEWED_SHELF_TITLE = "Recently viewed"

class HomeFeedRepository(
	private val dataSource: DataSource,
) {

	private val recipeRepository = RecipeRepository(dataSource)

	private val collectionRepository = HomeCollectionRepository(recipeRepository)

	private val featureRequestRepository = FeatureRequestRepository(dataSource)

	fun loadHomeFeed(userId: Long?, localHour: Int): HomeFeed {
		val mealType = mealTypeForLocalHour(localHour)
		val shelves = dataSource.connection.use { connection ->
			val dietaryPreferences = loadDietaryPreferences(connection, userId)
			buildShelves(connection, userId, mealType, dietaryPreferences)
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
			val dietaryPreferences = loadDietaryPreferences(connection, userId)
			when (shelfId) {
				HomeShelfId.FAVORITES -> favoriteShelfPage(
					connection = connection,
					userId = userId,
					window = window,
					dietaryPreferences = dietaryPreferences,
				)

				HomeShelfId.RECENTLY_VIEWED -> recentlyViewedShelfPage(
					connection = connection,
					userId = userId,
					window = window,
					dietaryPreferences = dietaryPreferences,
				)

				HomeShelfId.HABIT -> habitShelfPage(
					connection = connection,
					userId = userId,
					window = window,
					habitCuisine = null,
					dietaryPreferences = dietaryPreferences,
				)

				HomeShelfId.NEW,
				HomeShelfId.RIGHT_NOW,
				HomeShelfId.QUICK,
					-> publicShelfPage(
					connection = connection,
					userId = userId,
					mealType = mealType,
					shelfId = shelfId,
					window = window,
					dietaryPreferences = dietaryPreferences,
				)

				HomeShelfId.COLLECTION -> emptyShelfPage(window)
			}
		}
	}

	fun loadHabitCuisinePage(
		userId: Long?,
		habitCuisine: String,
		pageNumber: Int,
		pageSize: Int,
	): SearchResultsPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		val window = PageWindow(
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
		)
		return dataSource.connection.use { connection ->
			val dietaryPreferences = loadDietaryPreferences(connection, userId)
			habitShelfPage(
				connection = connection,
				userId = userId,
				window = window,
				habitCuisine = habitCuisine,
				dietaryPreferences = dietaryPreferences,
			)
		}
	}

	fun loadCollectionPage(
		userId: Long?,
		slug: String,
		pageNumber: Int,
		pageSize: Int,
	): SearchResultsPage? {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		return dataSource.connection.use { connection ->
			val dietaryPreferences = loadDietaryPreferences(connection, userId)
			collectionRepository.page(
				connection = connection,
				userId = userId,
				slug = slug,
				pageNumber = normalizedPageNumber,
				pageSize = normalizedPageSize,
				dietaryPreferences = dietaryPreferences,
			)
		}
	}

	private fun buildShelves(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
		dietaryPreferences: Set<DietaryPreference>,
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
					) + dietaryConditions(dietaryPreferences),
					params = dietaryParams(dietaryPreferences),
					order = SearchRecipeOrder.NEWEST,
				),
			)
			if (newest.isNotEmpty()) {
				add(
					HomeShelf(
						id = HomeShelfId.NEW,
						title = NEW_SHELF_TITLE,
						filters = SearchFilters(dietaryPreferences = dietaryPreferences),
						recipes = newest,
					),
				)
			}
			val rightNow = queryMealShelf(connection, userId, mealType, dietaryPreferences)
			if (rightNow.isNotEmpty()) {
				add(
					HomeShelf(
						id = HomeShelfId.RIGHT_NOW,
						title = mealType.displayName,
						filters = SearchFilters(
							mealTypes = setOf(mealType),
							dietaryPreferences = dietaryPreferences,
						),
						recipes = rightNow,
					),
				)
			}
			val quick = queryQuickShelf(connection, userId, mealType, dietaryPreferences)
			if (quick != null) {
				add(quick)
			}
			if (userId != null) {
				val recentlyViewed = queryRecentlyViewedShelf(connection, userId, dietaryPreferences)
				if (recentlyViewed.isNotEmpty()) {
					add(
						HomeShelf(
							id = HomeShelfId.RECENTLY_VIEWED,
							title = RECENTLY_VIEWED_SHELF_TITLE,
							filters = SearchFilters(dietaryPreferences = dietaryPreferences),
							recipes = recentlyViewed,
						),
					)
				}
				val habit = queryHabitShelf(connection, userId, dietaryPreferences)
				if (habit != null) {
					add(habit)
				}
				val excludedCuisines = habit?.filters?.cuisines.orEmpty()
				val activityLimit = HABIT_SHELF_LIMIT - if (habit == null) 0 else 1
				addAll(
					queryActivityHabitShelves(
						connection = connection,
						userId = userId,
						excludedCuisines = excludedCuisines,
						limit = activityLimit,
						dietaryPreferences = dietaryPreferences,
					),
				)
				val favorites = queryFavoriteShelf(connection, userId, dietaryPreferences)
				if (favorites.isNotEmpty()) {
					add(
						HomeShelf(
							id = HomeShelfId.FAVORITES,
							title = FAVORITES_SHELF_TITLE,
							filters = SearchFilters(dietaryPreferences = dietaryPreferences),
							recipes = favorites,
						),
					)
				}
			}
			addAll(
				collectionRepository.liveShelves(
					connection = connection,
					userId = userId,
					dietaryPreferences = dietaryPreferences,
				),
			)
		}
	}

	private fun queryMealShelf(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
		dietaryPreferences: Set<DietaryPreference>,
	): List<RecipeSummary> {
		return queryPublicShelf(
			connection = connection,
			userId = userId,
			query = ShelfQuery(
				extraConditions = listOf("r.meal_type = ?") + dietaryConditions(dietaryPreferences),
				params = listOf(mealType.name) + dietaryParams(dietaryPreferences),
				order = SearchRecipeOrder.CATALOG,
			),
		)
	}

	private fun queryQuickShelf(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
		dietaryPreferences: Set<DietaryPreference>,
	): HomeShelf? {
		val conditions = quickConditions() + dietaryConditions(dietaryPreferences)
		val params = listOf(mealType.name) + dietaryParams(dietaryPreferences)
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
					dietaryPreferences = dietaryPreferences,
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
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
		limit: Int = HOME_SHELF_PAGE_SIZE,
		offset: Int = 0,
	): List<RecipeSummary> {
		val extraConditions = dietaryConditions(dietaryPreferences)
		val extraWhere = if (extraConditions.isEmpty()) "" else " AND " + extraConditions.joinToString(" AND ")
		val sql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system, r.is_private,
				${recipeCompletenessScoreSql("r")} AS completeness_score,
				r.created_at
			FROM recipes r
			INNER JOIN favorites fav ON fav.recipe_id = r.id
			WHERE fav.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
				$extraWhere
			ORDER BY fav.created_at DESC, r.id DESC
			LIMIT ? OFFSET ?
		""".trimIndent()
		val recipes = connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setLong(paramIndex++, userId)
			statement.setLong(paramIndex++, userId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
			statement.setInt(paramIndex++, limit)
			statement.setInt(paramIndex++, offset)
			recipeRepository.readSearchRecipeCandidates(statement).map { candidate ->
				candidate.summary.copy(isFavorite = true)
			}
		}
		return recipes
	}

	private fun queryRecentlyViewedShelf(
		connection: Connection,
		userId: Long,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
		limit: Int = HOME_SHELF_PAGE_SIZE,
		offset: Int = 0,
	): List<RecipeSummary> {
		val extraConditions = dietaryConditions(dietaryPreferences)
		val extraWhere = if (extraConditions.isEmpty()) "" else " AND " + extraConditions.joinToString(" AND ")
		val sql = """
			SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system, r.is_private,
				${recipeCompletenessScoreSql("r")} AS completeness_score,
				r.created_at
			FROM recipes r
			INNER JOIN (
				SELECT recipe_id, MAX(created_at) AS viewed_at
				FROM recipe_activity
				WHERE user_id = ? AND event = ?
				GROUP BY recipe_id
			) viewed ON viewed.recipe_id = r.id
			WHERE (r.is_private = FALSE OR r.created_by_user_id = ?)
				$extraWhere
			ORDER BY viewed.viewed_at DESC, r.id DESC
			LIMIT ? OFFSET ?
		""".trimIndent()
		val recipes = connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setLong(paramIndex++, userId)
			statement.setString(paramIndex++, RecipeActivityEvent.OPEN.name)
			statement.setLong(paramIndex++, userId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
			statement.setInt(paramIndex++, limit)
			statement.setInt(paramIndex++, offset)
			recipeRepository.readSearchRecipeCandidates(statement).map { candidate -> candidate.summary }
		}
		return markFavorites(
			connection = connection,
			userId = userId,
			recipes = recipes,
		)
	}

	private fun queryHabitShelf(
		connection: Connection,
		userId: Long,
		dietaryPreferences: Set<DietaryPreference>,
	): HomeShelf? {
		val cuisine = dominantFavoriteCuisine(connection, userId) ?: return null
		val recipes = queryPublicShelf(
			connection = connection,
			userId = userId,
			query = habitShelfQuery(cuisine, dietaryPreferences),
		)
		return if (recipes.isEmpty()) {
			null
		} else {
			HomeShelf(
				id = HomeShelfId.HABIT,
				title = cuisine.displayName,
				filters = SearchFilters(
					cuisines = setOf(cuisine),
					dietaryPreferences = dietaryPreferences,
				),
				recipes = recipes,
			)
		}
	}

	private fun queryActivityHabitShelves(
		connection: Connection,
		userId: Long,
		excludedCuisines: Set<Cuisine>,
		limit: Int,
		dietaryPreferences: Set<DietaryPreference>,
	): List<HomeShelf> {
		if (limit <= 0) {
			return emptyList()
		}
		return loadActivityCuisineCandidates(connection, userId)
			.filter { candidate -> candidate.cuisine !in excludedCuisines }
			.sortedWith(activityCuisineOrder().reversed())
			.mapNotNull { candidate ->
				activityHabitShelf(
					connection = connection,
					userId = userId,
					cuisine = candidate.cuisine,
					dietaryPreferences = dietaryPreferences,
				)
			}
			.take(limit)
	}

	private fun activityHabitShelf(
		connection: Connection,
		userId: Long,
		cuisine: Cuisine,
		dietaryPreferences: Set<DietaryPreference>,
	): HomeShelf? {
		val recipes = queryPublicShelf(
			connection = connection,
			userId = userId,
			query = habitShelfQuery(cuisine, dietaryPreferences),
		)
		return if (recipes.isEmpty()) {
			null
		} else {
			HomeShelf(
				id = HomeShelfId.HABIT,
				title = cuisine.displayName,
				filters = SearchFilters(
					cuisines = setOf(cuisine),
					dietaryPreferences = dietaryPreferences,
				),
				recipes = recipes,
				habitCuisine = cuisine.displayName,
			)
		}
	}

	private fun habitShelfPage(
		connection: Connection,
		userId: Long?,
		window: PageWindow,
		habitCuisine: String?,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): SearchResultsPage {
		val cuisine = when {
			habitCuisine != null -> cuisineFromRawValue(habitCuisine)
			userId == null -> null
			else -> dominantFavoriteCuisine(connection, userId)
		}
		val query = if (cuisine == null) null else habitShelfQuery(cuisine, dietaryPreferences)
		val totalMatches = if (query == null) {
			0
		} else {
			countSearchWithFiltersRecipes(
				connection,
				publicWhereClause(query.extraConditions),
				query.params,
			)
		}
		val recipes = if (query == null) {
			emptyList()
		} else {
			queryPublicShelf(
				connection = connection,
				userId = userId,
				query = query,
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

	private fun dominantFavoriteCuisine(connection: Connection, userId: Long): Cuisine? {
		val candidates = loadFavoriteCuisineSaves(connection, userId)
			.groupBy { save -> save.cuisine }
			.map { (cuisine, saves) ->
				HabitCuisineCandidate(
					cuisine = cuisine,
					favoriteCount = saves.size,
					latestFavoriteAtMillis = saves.maxOf { save -> save.createdAtMillis },
				)
			}
			.filter { candidate -> candidate.favoriteCount >= HABIT_SHELF_MINIMUM_FAVORITES }
		return candidates.maxWithOrNull(habitCuisineOrder())?.cuisine
	}

	private fun loadActivityCuisineCandidates(
		connection: Connection,
		userId: Long,
	): List<ActivityCuisineCandidate> {
		val sql = """
			SELECT r.cuisine, COUNT(*) AS event_count, MAX(a.created_at) AS latest_event_at
			FROM recipe_activity a
			INNER JOIN recipes r ON r.id = a.recipe_id
			WHERE a.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
			GROUP BY r.cuisine
			HAVING COUNT(*) >= $ACTIVITY_HABIT_MINIMUM_EVENTS
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			statement.setLong(1, userId)
			statement.setLong(2, userId)
			statement.executeQuery().use { resultSet ->
				readActivityCuisineCandidates(resultSet)
			}
		}
	}

	private fun loadFavoriteCuisineSaves(connection: Connection, userId: Long): List<FavoriteCuisineSave> {
		val sql = """
			SELECT r.cuisine, fav.created_at
			FROM favorites fav
			INNER JOIN recipes r ON r.id = fav.recipe_id
			WHERE fav.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			statement.setLong(1, userId)
			statement.setLong(2, userId)
			statement.executeQuery().use { resultSet ->
				readFavoriteCuisineSaves(resultSet)
			}
		}
	}

	private fun publicShelfPage(
		connection: Connection,
		userId: Long?,
		mealType: MealType,
		shelfId: HomeShelfId,
		window: PageWindow,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): SearchResultsPage {
		val query = publicShelfQuery(shelfId, mealType, dietaryPreferences)
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
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): SearchResultsPage {
		val totalMatches = if (userId == null) 0 else countFavorites(connection, userId, dietaryPreferences)
		val recipes = if (userId == null) {
			emptyList()
		} else {
			queryFavoriteShelf(
				connection = connection,
				userId = userId,
				dietaryPreferences = dietaryPreferences,
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

	private fun recentlyViewedShelfPage(
		connection: Connection,
		userId: Long?,
		window: PageWindow,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): SearchResultsPage {
		val totalMatches = if (userId == null) 0 else countRecentlyViewed(connection, userId, dietaryPreferences)
		val recipes = if (userId == null) {
			emptyList()
		} else {
			queryRecentlyViewedShelf(
				connection = connection,
				userId = userId,
				dietaryPreferences = dietaryPreferences,
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

	private fun publicShelfQuery(
		shelfId: HomeShelfId,
		mealType: MealType,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): ShelfQuery {
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
				) + dietaryConditions(dietaryPreferences),
				params = dietaryParams(dietaryPreferences),
				order = SearchRecipeOrder.NEWEST,
			)

			HomeShelfId.RIGHT_NOW -> ShelfQuery(
				extraConditions = listOf("r.meal_type = ?") + dietaryConditions(dietaryPreferences),
				params = listOf(mealType.name) + dietaryParams(dietaryPreferences),
				order = SearchRecipeOrder.CATALOG,
			)

			HomeShelfId.QUICK -> ShelfQuery(
				extraConditions = quickConditions() + dietaryConditions(dietaryPreferences),
				params = listOf(mealType.name) + dietaryParams(dietaryPreferences),
				order = SearchRecipeOrder.CATALOG,
			)

			HomeShelfId.FAVORITES,
			HomeShelfId.HABIT,
			HomeShelfId.RECENTLY_VIEWED,
			HomeShelfId.COLLECTION,
				-> throw IllegalArgumentException("This home shelf is loaded separately")
		}
	}

	private fun emptyShelfPage(window: PageWindow): SearchResultsPage {
		return SearchResultsPage(
			items = emptyList(),
			pageNumber = window.pageNumber,
			pageSize = window.pageSize,
			totalMatches = 0,
		)
	}

	private fun countRecentlyViewed(
		connection: Connection,
		userId: Long,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): Int {
		val extraConditions = dietaryConditions(dietaryPreferences)
		val extraWhere = if (extraConditions.isEmpty()) "" else " AND " + extraConditions.joinToString(" AND ")
		val sql = """
			SELECT COUNT(*)
			FROM recipes r
			INNER JOIN (
				SELECT recipe_id
				FROM recipe_activity
				WHERE user_id = ? AND event = ?
				GROUP BY recipe_id
			) viewed ON viewed.recipe_id = r.id
			WHERE (r.is_private = FALSE OR r.created_by_user_id = ?)
				$extraWhere
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setLong(paramIndex++, userId)
			statement.setString(paramIndex++, RecipeActivityEvent.OPEN.name)
			statement.setLong(paramIndex++, userId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.getInt(1) else 0
			}
		}
	}

	private fun countFavorites(
		connection: Connection,
		userId: Long,
		dietaryPreferences: Set<DietaryPreference> = emptySet(),
	): Int {
		val extraConditions = dietaryConditions(dietaryPreferences)
		val extraWhere = if (extraConditions.isEmpty()) "" else " AND " + extraConditions.joinToString(" AND ")
		val sql = """
			SELECT COUNT(*)
			FROM recipes r
			INNER JOIN favorites fav ON fav.recipe_id = r.id
			WHERE fav.user_id = ?
				AND (r.is_private = FALSE OR r.created_by_user_id = ?)
				$extraWhere
		""".trimIndent()
		return connection.prepareStatement(sql).use { statement ->
			var paramIndex = 1
			statement.setLong(paramIndex++, userId)
			statement.setLong(paramIndex++, userId)
			dietaryPreferences.forEach { pref ->
				statement.setString(paramIndex++, pref.name)
			}
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

private data class FavoriteCuisineSave(
	val cuisine: Cuisine,
	val createdAtMillis: Long,
)

private data class HabitCuisineCandidate(
	val cuisine: Cuisine,
	val favoriteCount: Int,
	val latestFavoriteAtMillis: Long,
)

private data class ActivityCuisineCandidate(
	val cuisine: Cuisine,
	val eventCount: Int,
	val latestEventAtMillis: Long,
)

private fun habitShelfQuery(
	cuisine: Cuisine,
	dietaryPreferences: Set<DietaryPreference> = emptySet(),
): ShelfQuery {
	return ShelfQuery(
		extraConditions = listOf("r.cuisine = ?") + dietaryConditions(dietaryPreferences),
		params = listOf(cuisine.displayName) + dietaryParams(dietaryPreferences),
		order = SearchRecipeOrder.CATALOG,
	)
}

private fun habitCuisineOrder(): Comparator<HabitCuisineCandidate> {
	return compareBy<HabitCuisineCandidate> { candidate -> candidate.favoriteCount }
		.thenBy { candidate -> candidate.latestFavoriteAtMillis }
		.thenByDescending { candidate -> candidate.cuisine.displayName }
}

private fun activityCuisineOrder(): Comparator<ActivityCuisineCandidate> {
	return compareBy<ActivityCuisineCandidate> { candidate -> candidate.eventCount }
		.thenBy { candidate -> candidate.latestEventAtMillis }
		.thenByDescending { candidate -> candidate.cuisine.displayName }
}

private fun readActivityCuisineCandidates(resultSet: ResultSet): List<ActivityCuisineCandidate> {
	return buildList {
		while (resultSet.next()) {
			val cuisine = cuisineFromRawValue(resultSet.getString("cuisine")) ?: continue
			add(
				ActivityCuisineCandidate(
					cuisine = cuisine,
					eventCount = resultSet.getInt("event_count"),
					latestEventAtMillis = resultSet.getTimestamp("latest_event_at").time,
				),
			)
		}
	}
}

private fun readFavoriteCuisineSaves(resultSet: ResultSet): List<FavoriteCuisineSave> {
	return buildList {
		while (resultSet.next()) {
			val cuisine = cuisineFromRawValue(resultSet.getString("cuisine")) ?: continue
			add(
				FavoriteCuisineSave(
					cuisine = cuisine,
					createdAtMillis = resultSet.getTimestamp("created_at").time,
				),
			)
		}
	}
}

private const val GET_SEARCH_FILTERS_SQL = """
	SELECT filters_json
	FROM search_filters
	WHERE user_id = ?
"""

private val homeFeedJson = Json { ignoreUnknownKeys = true }

private fun loadDietaryPreferences(connection: Connection, userId: Long?): Set<DietaryPreference> {
	if (userId == null) return emptySet()
	return connection.prepareStatement(GET_SEARCH_FILTERS_SQL).use { statement ->
		statement.setLong(1, userId)
		statement.executeQuery().use { resultSet ->
			if (resultSet.next()) {
				val json = resultSet.getString("filters_json")
				runCatching {
					homeFeedJson.decodeFromString<SearchFilters>(json).dietaryPreferences
				}.getOrDefault(emptySet())
			} else {
				emptySet()
			}
		}
	}
}

private fun dietaryConditions(dietaryPreferences: Set<DietaryPreference>): List<String> {
	return dietaryPreferences.map { "? = ANY(r.dietary_preferences)" }
}

private fun dietaryParams(dietaryPreferences: Set<DietaryPreference>): List<Any> {
	return dietaryPreferences.map { it.name }
}

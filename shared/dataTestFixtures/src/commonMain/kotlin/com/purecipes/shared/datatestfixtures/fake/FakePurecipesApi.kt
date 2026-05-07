package com.purecipes.shared.datatestfixtures.fake

import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.CookbookCreateRequest
import com.purecipes.shared.domain.model.CookbookListPage
import com.purecipes.shared.domain.model.CookbookRef
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.PantryDelta
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchRequest
import com.purecipes.shared.domain.model.SearchResultsPage
import kotlin.time.Clock

class FakePurecipesApi(
	var searchResult: List<RecipeSummary> = emptyList(),
	var favoriteRecipes: List<RecipeSummary> = emptyList(),
	initialCookbooks: List<CookbookSummary> = emptyList(),
	initialRecipeDetails: List<RecipeDetails> = emptyList(),
	initialMeasurementPreferences: MeasurementPreferences = MeasurementPreferences(
		preferredSystem = MeasurementSystem.METRIC,
	),
	initialSearchFilters: SearchFilters = SearchFilters(),
	private val session: AuthenticatedSession = defaultAuthenticatedSession(),
) : PurecipesApi {

	private val recipeDetailsById = initialRecipeDetails.associateBy { it.id }.toMutableMap()
	private val createdRecipes = initialRecipeDetails.toMutableList()
	private val cookbooks = initialCookbooks.toMutableList()
	private val cookbookRecipeIds = mutableMapOf<Int, MutableSet<Int>>()
	private var nextCookbookId: Int = (initialCookbooks.maxOfOrNull { it.id } ?: 0) + 1

	init {
		for (c in initialCookbooks) {
			cookbookRecipeIds.getOrPut(c.id) { mutableSetOf() }
		}
	}

	var searchWithFiltersCalls: Int = 0
		private set

	val addedFavoriteIds = mutableListOf<Int>()
	val removedFavoriteIds = mutableListOf<Int>()
	val createdRecipeRequests = mutableListOf<RecipeWriteRequest>()
	val updatedRecipeRequests = mutableListOf<Pair<Int, RecipeWriteRequest>>()
	val savedMeasurementPreferences = mutableListOf<MeasurementPreferences>()
	val savedSearchFiltersList = mutableListOf<SearchFilters>()

	var measurementPreferences: MeasurementPreferences = initialMeasurementPreferences
		private set

	var searchFilters: SearchFilters = initialSearchFilters
		private set

	var userPantry: Set<String> = emptySet()
		private set

	override suspend fun searchWithFilters(request: SearchRequest): SearchResultsPage {
		searchWithFiltersCalls += 1
		val pageNumber = request.pageNumber.coerceAtLeast(1)
		val pageSize = request.pageSize.coerceAtLeast(1)
		val offset = (pageNumber - 1) * pageSize
		val paginatedResult = searchResult.drop(offset).take(pageSize)
		return SearchResultsPage(
			items = paginatedResult,
			pageNumber = pageNumber,
			pageSize = pageSize,
			totalMatches = searchResult.size,
		)
	}

	override suspend fun getRecipeDetails(recipeId: Int): RecipeDetails {
		return recipeDetailsById[recipeId] ?: error("No recipe found for id $recipeId")
	}

	override suspend fun getCreatedRecipes(): List<RecipeDetails> = createdRecipes.toList()

	override suspend fun createRecipe(request: RecipeWriteRequest): RecipeDetails {
		createdRecipeRequests += request
		val recipe = request.toRecipeDetails(id = nextRecipeId())
		storeRecipe(recipe)
		return recipe
	}

	override suspend fun updateRecipe(recipeId: Int, request: RecipeWriteRequest): RecipeDetails {
		updatedRecipeRequests += recipeId to request
		val recipe = request.toRecipeDetails(id = recipeId)
		storeRecipe(recipe)
		return recipe
	}

	override suspend fun signInWithGoogle(request: GoogleSignInRequest): AuthenticatedSession = session

	override suspend fun getCurrentSession(): AuthenticatedSession = session

	override suspend fun signOut() = Unit

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): SearchResultsPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceAtLeast(1)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		val slice = favoriteRecipes.drop(offset).take(normalizedPageSize)
		return SearchResultsPage(
			items = slice,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = favoriteRecipes.size,
		)
	}

	override suspend fun addFavorite(recipeId: Int) {
		addedFavoriteIds += recipeId
	}

	override suspend fun removeFavorite(recipeId: Int) {
		removedFavoriteIds += recipeId
		cookbookRecipeIds.values.forEach { it.remove(recipeId) }
	}

	override suspend fun getCookbooks(pageNumber: Int, pageSize: Int): CookbookListPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceAtLeast(1)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		val summaries = cookbooks.map { summary ->
			val count = cookbookRecipeIds[summary.id]?.count { recipeId ->
				favoriteRecipes.any { it.id == recipeId }
			} ?: 0
			summary.copy(recipeCount = count)
		}
		val slice = summaries.drop(offset).take(normalizedPageSize)
		return CookbookListPage(
			items = slice,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = summaries.size,
		)
	}

	override suspend fun createCookbook(request: CookbookCreateRequest): CookbookSummary {
		val now = Clock.System.now().toEpochMilliseconds()
		val summary = CookbookSummary(
			id = nextCookbookId++,
			name = request.name.trim(),
			recipeCount = 0,
			updatedAtEpochMillis = now,
		)
		cookbooks += summary
		cookbookRecipeIds[summary.id] = mutableSetOf()
		return summary
	}

	override suspend fun deleteCookbook(cookbookId: Int) {
		val recipeCount = cookbookRecipeIds[cookbookId].orEmpty().count { recipeId ->
			favoriteRecipes.any { it.id == recipeId }
		}
		check(recipeCount == 0) { "Only empty cookbooks can be deleted" }
		cookbooks.removeAll { it.id == cookbookId }
		cookbookRecipeIds.remove(cookbookId)
	}

	override suspend fun getCookbookRecipes(cookbookId: Int, pageNumber: Int, pageSize: Int): SearchResultsPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceAtLeast(1)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		val ids = cookbookRecipeIds[cookbookId].orEmpty()
		val recipes = ids.mapNotNull { id -> favoriteRecipes.find { it.id == id } }
			.sortedBy { it.id }
		val slice = recipes.drop(offset).take(normalizedPageSize)
		return SearchResultsPage(
			items = slice,
			pageNumber = normalizedPageNumber,
			pageSize = normalizedPageSize,
			totalMatches = recipes.size,
		)
	}

	override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int) {
		cookbookRecipeIds.getOrPut(cookbookId) { mutableSetOf() } += recipeId
	}

	override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int) {
		cookbookRecipeIds[cookbookId]?.remove(recipeId)
	}

	override suspend fun getRecipeCookbooks(recipeId: Int): List<CookbookRef> {
		return cookbooks.filter { cookbookRecipeIds[it.id]?.contains(recipeId) == true }
			.map { CookbookRef(id = it.id, name = it.name) }
	}

	override suspend fun getMeasurementPreferences(): MeasurementPreferences = measurementPreferences

	override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences): MeasurementPreferences {
		savedMeasurementPreferences += preferences
		measurementPreferences = preferences
		return preferences
	}

	override suspend fun getSearchFilters(): SearchFilters = searchFilters

	override suspend fun saveSearchFilters(filters: SearchFilters): SearchFilters {
		savedSearchFiltersList += filters
		searchFilters = filters
		return filters
	}

	override suspend fun getUserPantry(): Set<String> = userPantry

	override suspend fun updateUserPantry(delta: PantryDelta): Set<String> {
		userPantry = (userPantry + delta.add) - delta.remove
		return userPantry
	}

	fun storeRecipe(recipe: RecipeDetails) {
		recipeDetailsById[recipe.id] = recipe
		createdRecipes.removeAll { it.id == recipe.id }
		createdRecipes += recipe
	}

	private fun nextRecipeId(): Int = (createdRecipes.maxOfOrNull { it.id } ?: 0) + 1

	private fun RecipeWriteRequest.toRecipeDetails(id: Int): RecipeDetails {
		return RecipeDetails(
			id = id,
			title = title,
			description = description,
			imageUrl = imageUrl,
			ingredientGroups = ingredientGroups,
			steps = steps,
			totalTime = totalTime,
			yields = yields,
			cuisine = cuisine,
		)
	}

	private companion object {

		fun defaultAuthenticatedSession(): AuthenticatedSession {
			return AuthenticatedSession(
				accessToken = "session-token",
				expiresAtEpochSeconds = 4_000_000_000,
				user = AuthenticatedBackendUser(
					id = "1",
					email = "user@example.com",
					displayName = "User",
					firstName = "User",
					familyName = "Example",
					profileImageUrl = null,
					provider = "GOOGLE",
				),
			)
		}
	}
}

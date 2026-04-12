package com.purecipes.shared.datatestfixtures.fake

import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchRequest

class FakePurecipesApi(
	var searchResult: List<RecipeSummary> = emptyList(),
	var favoriteRecipes: List<RecipeSummary> = emptyList(),
	initialRecipeDetails: List<RecipeDetails> = emptyList(),
	initialMeasurementPreferences: MeasurementPreferences = MeasurementPreferences(
		preferredSystem = MeasurementSystem.METRIC,
	),
	initialSearchFilters: SearchFilters = SearchFilters(),
	private val session: AuthenticatedSession = defaultAuthenticatedSession(),
) : PurecipesApi {

	private val recipeDetailsById = initialRecipeDetails.associateBy { it.id }.toMutableMap()
	private val createdRecipes = initialRecipeDetails.toMutableList()

	var searchCalls: Int = 0
		private set

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

	override suspend fun search(query: String, limit: Int): List<RecipeSummary> {
		searchCalls += 1
		return searchResult
	}

	override suspend fun searchWithFilters(request: SearchRequest): List<RecipeSummary> {
		searchWithFiltersCalls += 1
		return searchResult
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

	override suspend fun getFavorites(): List<RecipeSummary> = favoriteRecipes

	override suspend fun addFavorite(recipeId: Int) {
		addedFavoriteIds += recipeId
	}

	override suspend fun removeFavorite(recipeId: Int) {
		removedFavoriteIds += recipeId
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

	private val recipeDetailsById = initialRecipeDetails.associateBy { it.id }.toMutableMap()
	private val createdRecipes = initialRecipeDetails.toMutableList()

	var searchCalls: Int = 0
		private set

	val addedFavoriteIds = mutableListOf<Int>()
	val removedFavoriteIds = mutableListOf<Int>()
	val createdRecipeRequests = mutableListOf<RecipeWriteRequest>()
	val updatedRecipeRequests = mutableListOf<Pair<Int, RecipeWriteRequest>>()
	val savedMeasurementPreferences = mutableListOf<MeasurementPreferences>()

	var measurementPreferences: MeasurementPreferences = initialMeasurementPreferences
		private set

	override suspend fun search(query: String, limit: Int): List<RecipeSummary> {
		searchCalls += 1
		return searchResult
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

	override suspend fun getFavorites(): List<RecipeSummary> = favoriteRecipes

	override suspend fun addFavorite(recipeId: Int) {
		addedFavoriteIds += recipeId
	}

	override suspend fun removeFavorite(recipeId: Int) {
		removedFavoriteIds += recipeId
	}

	override suspend fun getMeasurementPreferences(): MeasurementPreferences = measurementPreferences

	override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences): MeasurementPreferences {
		savedMeasurementPreferences += preferences
		measurementPreferences = preferences
		return preferences
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

package com.purecipes.shared.data.network

import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface PurecipesApi {

	@GET("recipes/search")
	suspend fun search(
		@Query("query") query: String,
		@Query("limit") limit: Int = 25,
	): List<RecipeSummary>

	@GET("recipes/{id}")
	suspend fun getRecipeDetails(@Path("id") recipeId: Int): RecipeDetails

	@GET("recipes/mine")
	suspend fun getCreatedRecipes(): List<RecipeDetails>

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("recipes")
	suspend fun createRecipe(@Body request: RecipeWriteRequest): RecipeDetails

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PUT("recipes/{id}")
	suspend fun updateRecipe(@Path("id") recipeId: Int, @Body request: RecipeWriteRequest): RecipeDetails

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("auth/google")
	suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): AuthenticatedSession

	@GET("auth/session")
	suspend fun getCurrentSession(): AuthenticatedSession

	@POST("auth/sign-out")
	suspend fun signOut()

	@GET("favorites")
	suspend fun getFavorites(): List<RecipeSummary>

	@POST("favorites/{id}")
	suspend fun addFavorite(@Path("id") recipeId: Int)

	@DELETE("favorites/{id}")
	suspend fun removeFavorite(@Path("id") recipeId: Int)

	@GET("settings/measurement")
	suspend fun getMeasurementPreferences(): MeasurementPreferences

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PUT("settings/measurement")
	suspend fun saveMeasurementPreferences(@Body preferences: MeasurementPreferences): MeasurementPreferences
}

package com.purecipes.shared.data.network

import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
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

	@GET("favorites")
	suspend fun getFavorites(): List<RecipeSummary>

	@POST("favorites/{id}")
	suspend fun addFavorite(@Path("id") recipeId: Int)

	@DELETE("favorites/{id}")
	suspend fun removeFavorite(@Path("id") recipeId: Int)
}

package com.purecipes.shared.data.network

import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import de.jensklingenberg.ktorfit.http.GET
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
}

package com.purecipes.shared.data.network

import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.VerifiedGoogleUser
import de.jensklingenberg.ktorfit.http.Body
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

	@POST("auth/google")
	suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): VerifiedGoogleUser
}

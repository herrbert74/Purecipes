package app.purecipes.shared.data.network

import app.purecipes.shared.domain.model.CookbookCreateRequest
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookShareToken
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestCommentCreateRequest
import app.purecipes.shared.domain.model.FeatureRequestCreateRequest
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.PantryDelta
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeNutritionEstimateRequest
import app.purecipes.shared.domain.model.RecipeNutritionEstimateResponse
import app.purecipes.shared.domain.model.RecipeWriteRequest
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchRequest
import app.purecipes.shared.domain.model.SearchResultsPage
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface PurecipesApi : PurecipesAuthApi {

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("recipes/search")
	suspend fun searchWithFilters(@Body request: SearchRequest): SearchResultsPage

	@GET("recipes/{id}")
	suspend fun getRecipeDetails(@Path("id") recipeId: Int): RecipeDetails

	@GET("recipes/mine")
	suspend fun getCreatedRecipes(): List<RecipeDetails>

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("recipes/nutrition-estimate")
	suspend fun estimateRecipeNutrition(@Body request: RecipeNutritionEstimateRequest): RecipeNutritionEstimateResponse

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("recipes")
	suspend fun createRecipe(@Body request: RecipeWriteRequest): RecipeDetails

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PUT("recipes/{id}")
	suspend fun updateRecipe(@Path("id") recipeId: Int, @Body request: RecipeWriteRequest): RecipeDetails

	@DELETE("recipes/{id}")
	suspend fun deleteRecipe(@Path("id") recipeId: Int)

	@GET("favorites")
	suspend fun getFavoriteRecipesPage(
		@Query("pageNumber") pageNumber: Int = 1,
		@Query("pageSize") pageSize: Int = 20,
	): SearchResultsPage

	@POST("favorites/{id}")
	suspend fun addFavorite(@Path("id") recipeId: Int)

	@DELETE("favorites/{id}")
	suspend fun removeFavorite(@Path("id") recipeId: Int)

	@GET("cookbooks")
	suspend fun getCookbooks(
		@Query("pageNumber") pageNumber: Int = 1,
		@Query("pageSize") pageSize: Int = 20,
	): CookbookListPage

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("cookbooks")
	suspend fun createCookbook(@Body request: CookbookCreateRequest): CookbookSummary

	@DELETE("cookbooks/{id}")
	suspend fun deleteCookbook(@Path("id") cookbookId: Int)

	@GET("cookbooks/{id}/recipes")
	suspend fun getCookbookRecipes(
		@Path("id") cookbookId: Int,
		@Query("pageNumber") pageNumber: Int = 1,
		@Query("pageSize") pageSize: Int = 20,
	): SearchResultsPage

	@PUT("cookbooks/{id}/recipes/{recipeId}")
	suspend fun addRecipeToCookbook(
		@Path("id") cookbookId: Int,
		@Path("recipeId") recipeId: Int,
	)

	@DELETE("cookbooks/{id}/recipes/{recipeId}")
	suspend fun removeRecipeFromCookbook(
		@Path("id") cookbookId: Int,
		@Path("recipeId") recipeId: Int,
	)

	@GET("recipes/{id}/cookbooks")
	suspend fun getRecipeCookbooks(@Path("id") recipeId: Int): List<CookbookRef>

	@POST("cookbooks/{id}/share")
	suspend fun createCookbookShare(@Path("id") cookbookId: Int): CookbookShareToken

	@POST("cookbook-shares/{token}/import")
	suspend fun importCookbookShare(@Path("token") token: String): CookbookImportResult

	@GET("feature-requests")
	suspend fun getFeatureRequests(
		@Query("sort") sort: String,
		@Query("status") status: String? = null,
		@Query("pageNumber") pageNumber: Int = 1,
		@Query("pageSize") pageSize: Int = 20,
	): FeatureRequestListPage

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("feature-requests")
	suspend fun createFeatureRequest(@Body request: FeatureRequestCreateRequest): FeatureRequest

	@GET("feature-requests/{id}")
	suspend fun getFeatureRequest(@Path("id") requestId: Int): FeatureRequest

	@POST("feature-requests/{id}/vote")
	suspend fun toggleFeatureRequestVote(@Path("id") requestId: Int): FeatureRequest

	@GET("feature-requests/{id}/comments")
	suspend fun getFeatureRequestComments(@Path("id") requestId: Int): List<FeatureRequestComment>

	@Headers("Accept: application/json", "Content-Type: application/json")
	@POST("feature-requests/{id}/comments")
	suspend fun addFeatureRequestComment(
		@Path("id") requestId: Int,
		@Body request: FeatureRequestCommentCreateRequest,
	): FeatureRequestComment

	@GET("settings/measurement")
	suspend fun getMeasurementPreferences(): MeasurementPreferences

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PUT("settings/measurement")
	suspend fun saveMeasurementPreferences(@Body preferences: MeasurementPreferences): MeasurementPreferences

	@GET("settings/search-filters")
	suspend fun getSearchFilters(): SearchFilters

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PUT("settings/search-filters")
	suspend fun saveSearchFilters(@Body filters: SearchFilters): SearchFilters

	@GET("settings/pantry")
	suspend fun getUserPantry(): Set<String>

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PATCH("settings/pantry")
	suspend fun updateUserPantry(@Body delta: PantryDelta): Set<String>

	@GET("settings/excluded-ingredients")
	suspend fun getUserExcludedIngredients(): Set<String>

	@Headers("Accept: application/json", "Content-Type: application/json")
	@PATCH("settings/excluded-ingredients")
	suspend fun updateUserExcludedIngredients(@Body delta: ExcludedIngredientsDelta): Set<String>

	@GET("ingredients/match")
	suspend fun matchIngredient(@Query("name") name: String): IngredientMatchResponse
}

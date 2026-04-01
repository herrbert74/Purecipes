package com.purecipes.feature.newrecipe.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.data.repository.toRecipeWriteRequest
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.RecipeDetails

interface CreatedRecipeDataSource {

	interface Remote {
		suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>>

		suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails>
	}
}

class CreatedRecipeRemoteDataSource(
	private val api: PurecipesApi,
) : CreatedRecipeDataSource.Remote {

	override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> {
		return runCatchingApi {
			api.getCreatedRecipes()
		}
	}

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
		return runCatchingApi {
			val recipeWriteRequest = request.toRecipeWriteRequest()
			val recipeId = request.recipeId
			if (recipeId == null) {
				api.createRecipe(recipeWriteRequest)
			} else {
				api.updateRecipe(recipeId, recipeWriteRequest)
			}
		}
	}
}

package com.purecipes.feature.recipedetails.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.RecipeDetails

class RecipeDetailsRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeDetailsDataSource.Remote {

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = runCatchingApi {
		api.getRecipeDetails(recipeId)
	}
}

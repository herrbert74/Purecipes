package com.purecipes.feature.recipedetails.data.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.RecipeDetails

class RecipeDetailsAccessor(private val api: PurecipesApi) : RecipeDetailsRepository {

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = runCatchingApi {
		api.getRecipeDetails(recipeId)
	}
}

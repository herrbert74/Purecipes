package com.purecipes.feature.recipedetails.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.RecipeDetails

interface RecipeDetailsDataSource {

	interface Remote {
		suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails>
	}
}

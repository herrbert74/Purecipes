package com.purecipes.feature.recipedetails.domain.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.RecipeDetails

interface RecipeDetailsRepository {

	suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails>
}

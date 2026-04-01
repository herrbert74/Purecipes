package com.purecipes.feature.newrecipe.domain.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.shared.domain.model.RecipeDetails

interface CreatedRecipeRepository {

	suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>>

	suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails>
}

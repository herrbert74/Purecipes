package com.purecipes.feature.newrecipe.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.shared.domain.model.RecipeDetails

class SaveCreatedRecipeUseCase(
	private val repository: CreatedRecipeRepository,
) {

	suspend operator fun invoke(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
		return repository.saveCreatedRecipe(request)
	}
}

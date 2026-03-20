package com.purecipes.feature.recipedetails.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.RecipeDetails

class GetRecipeDetailsUseCase(
	private val repository: RecipeDetailsRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<RecipeDetails> {
		return repository.getRecipeDetails(recipeId)
	}
}

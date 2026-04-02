package com.purecipes.feature.newrecipe.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.shared.domain.model.RecipeDetails

class GetCreatedRecipesUseCase(
	private val repository: CreatedRecipeRepository,
) {

	suspend operator fun invoke(): Outcome<List<RecipeDetails>> {
		return repository.getCreatedRecipes()
	}
}

package app.purecipes.feature.recipedetails.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import app.purecipes.shared.domain.model.RecipeDetails

class GetRecipeDetailsUseCase(
	private val repository: RecipeDetailsRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<RecipeDetails> {
		return repository.getRecipeDetails(recipeId)
	}
}

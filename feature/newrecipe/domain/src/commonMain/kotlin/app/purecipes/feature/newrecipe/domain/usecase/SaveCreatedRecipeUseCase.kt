package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import app.purecipes.shared.domain.model.RecipeDetails

class SaveCreatedRecipeUseCase(
	private val repository: CreatedRecipeRepository,
) {

	suspend operator fun invoke(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
		return repository.saveCreatedRecipe(request)
	}
}

package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import dev.zacsweers.metro.Inject

@Inject
class DeleteCreatedRecipeUseCase(
	private val repository: CreatedRecipeRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<Unit> {
		return repository.deleteCreatedRecipe(recipeId)
	}
}

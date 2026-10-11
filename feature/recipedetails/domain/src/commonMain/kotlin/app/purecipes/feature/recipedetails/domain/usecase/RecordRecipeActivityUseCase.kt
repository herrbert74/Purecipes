package app.purecipes.feature.recipedetails.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.recipedetails.domain.repository.RecipeActivityRepository
import app.purecipes.shared.domain.model.RecipeActivityEvent
import dev.zacsweers.metro.Inject

@Inject
class RecordRecipeActivityUseCase(
	private val repository: RecipeActivityRepository,
) {

	suspend operator fun invoke(recipeId: Int, event: RecipeActivityEvent): Outcome<Unit> {
		return repository.record(recipeId, event)
	}
}

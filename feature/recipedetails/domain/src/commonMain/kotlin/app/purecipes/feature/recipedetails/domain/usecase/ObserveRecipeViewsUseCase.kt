package app.purecipes.feature.recipedetails.domain.usecase

import app.purecipes.feature.recipedetails.domain.repository.RecipeActivityRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveRecipeViewsUseCase(
	private val repository: RecipeActivityRepository,
) {

	operator fun invoke(): Flow<Int> = repository.observeRecipeViews()
}

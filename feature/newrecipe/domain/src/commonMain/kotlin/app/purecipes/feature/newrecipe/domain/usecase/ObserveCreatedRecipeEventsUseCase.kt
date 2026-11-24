package app.purecipes.feature.newrecipe.domain.usecase

import app.purecipes.feature.newrecipe.domain.model.CreatedRecipeEvent
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveCreatedRecipeEventsUseCase(
	private val repository: CreatedRecipeRepository,
) {

	operator fun invoke(): Flow<CreatedRecipeEvent> = repository.observeCreatedRecipeEvents()
}

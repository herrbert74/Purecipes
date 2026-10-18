package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveSearchFilterChangesUseCase(
	private val repository: RecipeSearchFilterRepository,
) {

	operator fun invoke(): Flow<Unit> = repository.observeFilterChanges()
}

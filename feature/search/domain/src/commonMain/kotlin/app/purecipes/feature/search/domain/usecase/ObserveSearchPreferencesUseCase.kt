package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.search.domain.repository.SearchPreferencesRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveSearchPreferencesUseCase(
	private val repository: SearchPreferencesRepository,
) {

	operator fun invoke(): Flow<SearchPreferences> = repository.observeSearchPreferences()
}

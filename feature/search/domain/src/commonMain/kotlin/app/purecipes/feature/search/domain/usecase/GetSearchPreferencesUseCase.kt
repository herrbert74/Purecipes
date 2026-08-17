package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.search.domain.repository.SearchPreferencesRepository
import dev.zacsweers.metro.Inject

@Inject
class GetSearchPreferencesUseCase(
	private val repository: SearchPreferencesRepository,
) {

	operator fun invoke(): SearchPreferences = repository.getSearchPreferences()
}

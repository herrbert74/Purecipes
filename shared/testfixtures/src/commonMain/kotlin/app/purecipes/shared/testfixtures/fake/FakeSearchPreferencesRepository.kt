package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.search.domain.repository.SearchPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSearchPreferencesRepository(
	defaults: SearchPreferences = SearchPreferences(),
) : SearchPreferencesRepository {

	private val flow = MutableStateFlow(defaults)

	override fun observeSearchPreferences(): Flow<SearchPreferences> = flow

	override fun getSearchPreferences(): SearchPreferences = flow.value

	override fun saveSearchPreferences(preferences: SearchPreferences) {
		flow.value = preferences
	}
}

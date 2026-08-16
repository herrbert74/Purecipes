package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.model.SearchPreferences
import kotlinx.coroutines.flow.Flow

interface SearchPreferencesDataSource {

	interface Local {

		fun observeSearchPreferences(): Flow<SearchPreferences>

		fun getSearchPreferences(): SearchPreferences

		fun saveSearchPreferences(preferences: SearchPreferences)
	}
}

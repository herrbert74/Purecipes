package app.purecipes.feature.search.domain.repository

import app.purecipes.feature.search.domain.model.SearchPreferences
import kotlinx.coroutines.flow.Flow

interface SearchPreferencesRepository {

	fun observeSearchPreferences(): Flow<SearchPreferences>

	fun getSearchPreferences(): SearchPreferences

	fun saveSearchPreferences(preferences: SearchPreferences)
}

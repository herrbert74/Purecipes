package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.SearchPreferencesDataSource
import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.feature.search.domain.repository.SearchPreferencesRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class)
class SearchPreferencesAccessor(
	private val localDataSource: SearchPreferencesDataSource.Local,
) : SearchPreferencesRepository {

	override fun observeSearchPreferences(): Flow<SearchPreferences> {
		return localDataSource.observeSearchPreferences()
	}

	override fun getSearchPreferences(): SearchPreferences {
		return localDataSource.getSearchPreferences()
	}

	override fun saveSearchPreferences(preferences: SearchPreferences) {
		localDataSource.saveSearchPreferences(preferences)
	}
}

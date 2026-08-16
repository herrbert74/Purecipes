package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.SearchPreferencesDataSource
import app.purecipes.feature.search.domain.model.SearchPreferences
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SearchPreferencesAccessorTest {

	@Test
	fun `get and save delegate to local data source`() = runTest {
		val local = FakeSearchPreferencesLocalDataSource()
		val accessor = SearchPreferencesAccessor(local)
		val preferences = SearchPreferences(applyRecipeFiltersToTitleSearch = false)

		accessor.saveSearchPreferences(preferences)

		accessor.getSearchPreferences() shouldBe preferences
		accessor.observeSearchPreferences().first() shouldBe preferences
	}

	private class FakeSearchPreferencesLocalDataSource : SearchPreferencesDataSource.Local {

		private val flow = MutableStateFlow(SearchPreferences())

		override fun observeSearchPreferences(): Flow<SearchPreferences> = flow

		override fun getSearchPreferences(): SearchPreferences = flow.value

		override fun saveSearchPreferences(preferences: SearchPreferences) {
			flow.value = preferences
		}
	}
}

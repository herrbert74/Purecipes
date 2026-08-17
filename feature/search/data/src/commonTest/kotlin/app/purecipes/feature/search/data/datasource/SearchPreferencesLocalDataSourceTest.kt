package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.model.SearchPreferences
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test

class SearchPreferencesLocalDataSourceTest {

	@Test
	fun `preferences stay in sync across datasource instances`() = runTest {
		val preferencesKey = "search.preferences.test.${Random.nextInt()}"
		val firstDataSource = SearchPreferencesLocalDataSource(preferencesKey = preferencesKey)
		val secondDataSource = SearchPreferencesLocalDataSource(preferencesKey = preferencesKey)
		val updatedPreferences = SearchPreferences(applyRecipeFiltersToTitleSearch = false)

		firstDataSource.saveSearchPreferences(updatedPreferences)

		secondDataSource.getSearchPreferences() shouldBe updatedPreferences
	}

	@Test
	fun `defaults to applying recipe filters on title search`() = runTest {
		val preferencesKey = "search.preferences.test.${Random.nextInt()}"
		val dataSource = SearchPreferencesLocalDataSource(preferencesKey = preferencesKey)

		dataSource.getSearchPreferences() shouldBe SearchPreferences()
	}
}

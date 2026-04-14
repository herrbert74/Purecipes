package com.purecipes.feature.search.data.datasource

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeSearchFilterRemoteDataSourceTest {

	@Test
	fun `getFilters returns filters from API`() = runTest {
		val expected = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val api = FakePurecipesApi(initialSearchFilters = expected)
		val dataSource = RecipeSearchFilterRemoteDataSource(api)

		val result = dataSource.getFilters()

		assertEquals(expected, result.get())
		assertNull(result.getError())
	}

	@Test
	fun `saveFilters stores filters via API`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = RecipeSearchFilterRemoteDataSource(api)
		val filters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))

		dataSource.saveFilters(filters)

		assertEquals(1, api.savedSearchFiltersList.size)
		assertEquals(filters, api.savedSearchFiltersList.first())
	}

	@Test
	fun `saveFilters returns saved filters`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = RecipeSearchFilterRemoteDataSource(api)
		val filters = SearchFilters(cuisines = setOf(Cuisine.MEXICAN))

		val result = dataSource.saveFilters(filters)

		assertEquals(filters, result.get())
		assertNull(result.getError())
	}
}

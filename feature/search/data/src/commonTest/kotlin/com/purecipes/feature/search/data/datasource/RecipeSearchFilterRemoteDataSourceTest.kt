package com.purecipes.feature.search.data.datasource

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeSearchFilterRemoteDataSourceTest {

	@Test
	fun `getFilters returns filters from API`() = runTest {
		val expected = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val api = FakePurecipesApi(initialSearchFilters = expected)
		val dataSource = RecipeSearchFilterRemoteDataSource(api)

		val result = dataSource.getFilters()

		result.get() shouldBe expected
		result.getError() shouldBe null
	}

	@Test
	fun `saveFilters stores filters via API`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = RecipeSearchFilterRemoteDataSource(api)
		val filters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))

		dataSource.saveFilters(filters)

		api.savedSearchFiltersList.size shouldBe 1
		api.savedSearchFiltersList.first() shouldBe filters
	}

	@Test
	fun `saveFilters returns saved filters`() = runTest {
		val api = FakePurecipesApi()
		val dataSource = RecipeSearchFilterRemoteDataSource(api)
		val filters = SearchFilters(cuisines = setOf(Cuisine.MEXICAN))

		val result = dataSource.saveFilters(filters)

		result.get() shouldBe filters
		result.getError() shouldBe null
	}
}

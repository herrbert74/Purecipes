package com.purecipes.feature.search.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeSearchAccessorTest {

	@Test
	fun `search returns results from API`() = runTest {
		val expected = listOf(
			RecipeSummary(id = 1, title = "Tomato Soup", cuisine = Cuisine.AMERICAN, imageUrl = null, totalTime = 30),
		)
		val api = FakePurecipesApi(searchResult = expected)
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("tomato")

		assertEquals(expected, outcome.get())
		assertNull(outcome.getError())
		assertEquals(1, api.searchWithFiltersCalls)
	}

	@Test
	fun `blank query calls API and returns result`() = runTest {
		val api = FakePurecipesApi(searchResult = emptyList())
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("   ")

		assertEquals(emptyList(), outcome.get())
		assertNull(outcome.getError())
		assertEquals(1, api.searchWithFiltersCalls)
	}

	@Test
	fun `search passes filters to API`() = runTest {
		val api = FakePurecipesApi(searchResult = emptyList())
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		accessor.search("pasta", filters)

		assertEquals(1, api.searchWithFiltersCalls)
	}
}

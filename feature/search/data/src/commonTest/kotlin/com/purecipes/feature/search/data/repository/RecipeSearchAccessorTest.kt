package com.purecipes.feature.search.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeSearchAccessorTest {

	@Test
	fun `blank queries return empty list without calling api`() = runTest {
		val api = FakePurecipesApi(
			searchResult = listOf(
				RecipeSummary(
					id = 1,
					title = "Should not be used",
					cuisine = Cuisine.AMERICAN,
					imageUrl = null,
					totalTime = 10,
				)
			),
		)
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("   ")

		assertEquals(emptyList(), outcome.get())
		assertNull(outcome.getError())
		assertEquals(0, api.searchCalls)
	}
}

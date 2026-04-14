package com.purecipes.feature.search.domain.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchRecipesUseCaseTest {

	@Test
	fun `delegates to repository with query and default filters`() = runTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val useCase = SearchRecipesUseCase(repository)

		useCase("pasta")

		assertEquals("pasta", repository.lastQuery)
		assertEquals(SearchFilters(), repository.lastFilters)
	}

	@Test
	fun `delegates to repository with provided filters`() = runTest {
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val useCase = SearchRecipesUseCase(repository)

		useCase("pasta", filters)

		assertEquals(filters, repository.lastFilters)
	}

	@Test
	fun `returns repository result`() = runTest {
		val expected = listOf(
			RecipeSummary(id = 1, title = "Pasta", cuisine = Cuisine.ITALIAN, imageUrl = null, totalTime = 20),
		)
		val repository = FakeRecipeSearchRepository(Ok(expected))
		val useCase = SearchRecipesUseCase(repository)

		val result = useCase("pasta")

		assertEquals(expected, result.get())
	}

	private class FakeRecipeSearchRepository(
		private val result: SearchOutcome<List<RecipeSummary>>,
	) : RecipeSearchRepository {

		var lastQuery: String? = null
		var lastFilters: SearchFilters? = null

		override suspend fun search(query: String, filters: SearchFilters): SearchOutcome<List<RecipeSummary>> {
			lastQuery = query
			lastFilters = filters
			return result
		}
	}
}

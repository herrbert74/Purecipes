package com.purecipes.feature.search.domain.usecase

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchResultsPage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SearchRecipesUseCaseTest {

	@Test
	fun `delegates to repository with query and default filters`() = runTest {
		val repository = FakeRecipeSearchRepository(Ok(SearchResultsPage(emptyList(), 1, 20, 0)))
		val useCase = SearchRecipesUseCase(repository)

		useCase("pasta")

		repository.lastQuery shouldBe "pasta"
		repository.lastFilters shouldBe SearchFilters()
		repository.lastPageNumber shouldBe 1
		repository.lastPageSize shouldBe 20
	}

	@Test
	fun `delegates to repository with provided filters`() = runTest {
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val repository = FakeRecipeSearchRepository(Ok(SearchResultsPage(emptyList(), 1, 20, 0)))
		val useCase = SearchRecipesUseCase(repository)

		useCase("pasta", filters, pageNumber = 3, pageSize = 10)

		repository.lastFilters shouldBe filters
		repository.lastPageNumber shouldBe 3
		repository.lastPageSize shouldBe 10
	}

	@Test
	fun `returns repository result`() = runTest {
		val expected = listOf(
			RecipeSummary(id = 1, title = "Pasta", cuisine = Cuisine.ITALIAN, imageUrl = null, totalTime = 20),
		)
		val repository = FakeRecipeSearchRepository(Ok(SearchResultsPage(expected, 1, 20, expected.size)))
		val useCase = SearchRecipesUseCase(repository)

		val result = useCase("pasta")

		result.get()?.items shouldBe expected
	}

	private class FakeRecipeSearchRepository(
		private val result: SearchOutcome<SearchResultsPage>,
	) : RecipeSearchRepository {

		var lastQuery: String? = null
		var lastFilters: SearchFilters? = null
		var lastPageNumber: Int? = null
		var lastPageSize: Int? = null

		override suspend fun search(
			query: String,
			filters: SearchFilters,
			pageNumber: Int,
			pageSize: Int,
		): SearchOutcome<SearchResultsPage> {
			lastQuery = query
			lastFilters = filters
			lastPageNumber = pageNumber
			lastPageSize = pageSize
			return result
		}
	}
}

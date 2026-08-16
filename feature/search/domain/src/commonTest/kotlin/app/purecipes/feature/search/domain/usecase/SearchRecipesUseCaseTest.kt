package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
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

		useCase("pasta", filters, keyIngredients = setOf("Tomato"), pageNumber = 3, pageSize = 10)

		repository.lastFilters shouldBe filters
		repository.lastKeyIngredients shouldBe setOf("Tomato")
		repository.lastPageNumber shouldBe 3
		repository.lastPageSize shouldBe 10
		repository.lastApplyRecipeFilters shouldBe true
	}

	@Test
	fun `delegates recipe filter flag to repository`() = runTest {
		val repository = FakeRecipeSearchRepository(Ok(SearchResultsPage(emptyList(), 1, 20, 0)))
		val useCase = SearchRecipesUseCase(repository)

		useCase("chocolate", applyRecipeFilters = false)

		repository.lastApplyRecipeFilters shouldBe false
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
		var lastKeyIngredients: Set<String>? = null
		var lastPageNumber: Int? = null
		var lastPageSize: Int? = null
		var lastApplyRecipeFilters: Boolean? = null

		override suspend fun search(
			query: String,
			filters: SearchFilters,
			keyIngredients: Set<String>,
			pageNumber: Int,
			pageSize: Int,
			applyRecipeFilters: Boolean,
		): SearchOutcome<SearchResultsPage> {
			lastQuery = query
			lastFilters = filters
			lastKeyIngredients = keyIngredients
			lastPageNumber = pageNumber
			lastPageSize = pageSize
			lastApplyRecipeFilters = applyRecipeFilters
			return result
		}
	}
}

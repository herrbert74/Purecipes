package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeSearchAccessorTest {

	@Test
	fun `search returns results from API`() = runTest {
		val expected = listOf(
			RecipeSummary(id = 1, title = "Tomato Soup", cuisine = Cuisine.AMERICAN, imageUrl = null, totalTime = 30),
		)
		val api = FakePurecipesApi(searchResult = expected)
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("tomato", SearchFilters(), pageNumber = 1, pageSize = 20)

		outcome.get()?.items shouldBe expected
		outcome.getError() shouldBe null
		api.searchWithFiltersCalls shouldBe 1
	}

	@Test
	fun `blank query calls API and returns result`() = runTest {
		val api = FakePurecipesApi(searchResult = emptyList())
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("   ", SearchFilters(), pageNumber = 1, pageSize = 20)

		outcome.get()?.items shouldBe emptyList()
		outcome.getError() shouldBe null
		api.searchWithFiltersCalls shouldBe 1
	}

	@Test
	fun `search passes filters to API`() = runTest {
		val api = FakePurecipesApi(searchResult = emptyList())
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		accessor.search("pasta", filters, keyIngredients = setOf("Tomato"), pageNumber = 2, pageSize = 10)

		api.searchWithFiltersCalls shouldBe 1
		api.lastSearchRequest?.keyIngredients shouldBe setOf("Tomato")
	}
}

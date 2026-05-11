package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SaveSearchFiltersUseCaseTest {

	@Test
	fun `forwards filters to repository`() = runTest {
		val repository = FakeRecipeSearchFilterRepository()
		val useCase = SaveSearchFiltersUseCase(repository)
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		useCase(filters)

		repository.savedFilters shouldBe filters
	}

	@Test
	fun `overwrites previously saved filters`() = runTest {
		val repository = FakeRecipeSearchFilterRepository()
		val useCase = SaveSearchFiltersUseCase(repository)
		val first = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val second = SearchFilters(cuisines = setOf(Cuisine.FRENCH))

		useCase(first)
		useCase(second)

		repository.savedFilters shouldBe second
	}
}

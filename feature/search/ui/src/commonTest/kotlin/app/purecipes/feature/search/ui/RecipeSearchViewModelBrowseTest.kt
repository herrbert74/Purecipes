package app.purecipes.feature.search.ui

import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelBrowseTest {

	@Test
	fun onFiltersChangeWithSearchSavesFiltersAndSearches() = runViewModelTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			filterRepository = filterRepository,
			searchRepository = searchRepository,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size
		val filters = SearchFilters(mealTypes = setOf(MealType.BREAKFAST))

		viewModel.onFiltersChange(filters = filters, search = true)
		advanceUntilIdle()

		viewModel.activeFilters shouldBe filters
		filterRepository.savedFilters shouldBe filters
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}
}

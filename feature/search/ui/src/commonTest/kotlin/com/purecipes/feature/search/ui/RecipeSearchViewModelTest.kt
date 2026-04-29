package com.purecipes.feature.search.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelTest {

	@Test
	fun `search loads recipes and closes the search bar`() = runTest {
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 7,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
					),
				),
			),
		)
		val viewModel = makeViewModel(searchRepository = repository, coroutineScope = this)

		advanceUntilIdle()

		repository.queries shouldBe listOf("")
		viewModel.recipes.size shouldBe 1
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
		viewModel.isSearching shouldBe false
		viewModel.isSearchBarActive shouldBe false
		viewModel.errorMessage shouldBe null
	}

	@Test
	fun `search exposes repository error message`() = runTest {
		val repository = FakeRecipeSearchRepository(
			result = Err(Failure.ServerError("Search failed")),
		)
		val viewModel = makeViewModel(searchRepository = repository, coroutineScope = this)

		advanceUntilIdle()

		viewModel.recipes.isEmpty() shouldBe true
		viewModel.errorMessage shouldBe "Search failed"
	}

	@Test
	fun `search stores total matches from paged response`() = runTest {
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 11,
						title = "Page Result",
						cuisine = Cuisine.FRENCH,
						imageUrl = null,
						totalTime = 25,
					),
				),
			),
			totalMatches = 37,
		)
		val viewModel = makeViewModel(searchRepository = repository, coroutineScope = this)

		advanceUntilIdle()

		viewModel.totalMatches shouldBe 37
		viewModel.recipes.size shouldBe 1
	}

	@Test
	fun `search now sends updated query and first page request`() = runTest {
		val repository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = makeViewModel(searchRepository = repository, coroutineScope = this)
		advanceUntilIdle()

		viewModel.onSearchQueryChange("chicken")
		viewModel.searchNow()
		advanceUntilIdle()

		repository.lastQuery shouldBe "chicken"
		repository.lastPageNumber shouldBe 1
		repository.lastPageSize shouldBe 20
	}

	@Test
	fun `init uses default filters when saved filters are empty`() = runTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)

		advanceUntilIdle()

		viewModel.activeFilters.isEmpty shouldBe false
		viewModel.activeFilters shouldBe SearchFilters.default()
	}

	@Test
	fun `init uses saved filters when they are not empty`() = runTest {
		val saved = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = saved)
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)

		advanceUntilIdle()

		viewModel.activeFilters shouldBe saved
	}

	@Test
	fun `onFiltersChange updates active filters without saving`() = runTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)
		advanceUntilIdle()
		val savedAfterInit = filterRepository.savedFilters

		val newFilters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))
		viewModel.onFiltersChange(newFilters)
		advanceUntilIdle()

		viewModel.activeFilters shouldBe newFilters
		filterRepository.savedFilters shouldBe savedAfterInit
	}

	@Test
	fun `onFiltersChange does not trigger a new search`() = runTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = makeViewModel(searchRepository = searchRepository, coroutineScope = this)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onFiltersChange(SearchFilters(cuisines = setOf(Cuisine.FRENCH)))
		advanceUntilIdle()

		searchRepository.queries.size shouldBe searchCountAfterInit
	}

	@Test
	fun `filter sheet is hidden by default`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)

		viewModel.isFilterSheetVisible shouldBe false
	}

	@Test
	fun `onFilterButtonClick shows the filter sheet`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)

		viewModel.onFilterButtonClick()

		viewModel.isFilterSheetVisible shouldBe true
	}

	@Test
	fun `onFilterSheetDismiss hides the filter sheet`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)
		viewModel.onFilterButtonClick()

		viewModel.onFilterSheetDismiss()

		viewModel.isFilterSheetVisible shouldBe false
	}

	@Test
	fun `onFilterSheetDismiss saves filters and triggers search when filters changed`() = runTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
			coroutineScope = this,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		val newFilters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		viewModel.onFiltersChange(newFilters)
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		filterRepository.savedFilters shouldBe newFilters
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}

	@Test
	fun `onFilterSheetDismiss does not save or search when filters are unchanged`() = runTest {
		val saved = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = saved)
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
			coroutineScope = this,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size
		val savedAfterInit = filterRepository.savedFilters

		viewModel.onFilterButtonClick()
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		filterRepository.savedFilters shouldBe savedAfterInit
		searchRepository.queries.size shouldBe searchCountAfterInit
	}

	private fun makeViewModel(
		searchRepository: RecipeSearchRepository = FakeRecipeSearchRepository(Ok(emptyList())),
		filterRepository: RecipeSearchFilterRepository = FakeRecipeSearchFilterRepository(),
		coroutineScope: CoroutineScope? = null,
	) = RecipeSearchViewModel(
		filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
		getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
		searchRecipes = SearchRecipesUseCase(searchRepository),
		trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
		getSearchFilters = GetSearchFiltersUseCase(filterRepository),
		saveSearchFilters = SaveSearchFiltersUseCase(filterRepository),
		coroutineScope = coroutineScope,
	)
}

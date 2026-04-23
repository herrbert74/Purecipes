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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

		assertEquals(listOf(""), repository.queries)
		assertEquals(1, viewModel.recipes.size)
		assertEquals("Tomato Pasta", viewModel.recipes.single().title)
		assertFalse(viewModel.isSearching)
		assertFalse(viewModel.isSearchBarActive)
		assertNull(viewModel.errorMessage)
	}

	@Test
	fun `search exposes repository error message`() = runTest {
		val repository = FakeRecipeSearchRepository(
			result = Err(Failure.ServerError("Search failed")),
		)
		val viewModel = makeViewModel(searchRepository = repository, coroutineScope = this)

		advanceUntilIdle()

		assertTrue(viewModel.recipes.isEmpty())
		assertEquals("Search failed", viewModel.errorMessage)
	}

	@Test
	fun `init uses default filters when saved filters are empty`() = runTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)

		advanceUntilIdle()

		assertFalse(viewModel.activeFilters.isEmpty)
		assertEquals(SearchFilters.default(), viewModel.activeFilters)
	}

	@Test
	fun `init uses saved filters when they are not empty`() = runTest {
		val saved = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = saved)
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)

		advanceUntilIdle()

		assertEquals(saved, viewModel.activeFilters)
	}

	@Test
	fun `onFiltersChange updates active filters and saves them`() = runTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = makeViewModel(filterRepository = filterRepository, coroutineScope = this)
		advanceUntilIdle()

		val newFilters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))
		viewModel.onFiltersChange(newFilters)
		advanceUntilIdle()

		assertEquals(newFilters, viewModel.activeFilters)
		assertEquals(newFilters, filterRepository.savedFilters)
	}

	@Test
	fun `onFiltersChange triggers a new search`() = runTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = makeViewModel(searchRepository = searchRepository, coroutineScope = this)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onFiltersChange(SearchFilters(cuisines = setOf(Cuisine.FRENCH)))
		advanceUntilIdle()

		assertEquals(searchCountAfterInit + 1, searchRepository.queries.size)
	}

	@Test
	fun `filter sheet is hidden by default`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)

		assertFalse(viewModel.isFilterSheetVisible)
	}

	@Test
	fun `onFilterButtonClick shows the filter sheet`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)

		viewModel.onFilterButtonClick()

		assertTrue(viewModel.isFilterSheetVisible)
	}

	@Test
	fun `onFilterSheetDismiss hides the filter sheet`() = runTest {
		val viewModel = makeViewModel(coroutineScope = this)
		viewModel.onFilterButtonClick()

		viewModel.onFilterSheetDismiss()

		assertFalse(viewModel.isFilterSheetVisible)
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

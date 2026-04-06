package com.purecipes.feature.search.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
		val viewModel = RecipeSearchViewModel(
			filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
			searchRecipes = SearchRecipesUseCase(repository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			coroutineScope = this,
		)

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
		val viewModel = RecipeSearchViewModel(
			filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
			searchRecipes = SearchRecipesUseCase(repository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertTrue(viewModel.recipes.isEmpty())
		assertEquals("Search failed", viewModel.errorMessage)
	}

	private class FakeRecipeSearchRepository(
		private val result: SearchOutcome<List<RecipeSummary>>,
	) : RecipeSearchRepository {

		val queries = mutableListOf<String>()

		override suspend fun search(query: String): SearchOutcome<List<RecipeSummary>> {
			queries += query
			return result
		}
	}

	private class FakeMeasurementPreferencesRepository(
		private val preferences: MeasurementPreferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
		),
	) : MeasurementPreferencesRepository {

		private val flow = MutableStateFlow(preferences)

		override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = flow

		override suspend fun getMeasurementPreferences(): MeasurementPreferences = flow.value

		override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
			flow.value = preferences
		}

		override suspend fun resetMeasurementPreferences() {
			flow.value = this.preferences
		}

		override suspend fun markMismatchNotificationSeen(recipeId: Int) = Unit
	}

}

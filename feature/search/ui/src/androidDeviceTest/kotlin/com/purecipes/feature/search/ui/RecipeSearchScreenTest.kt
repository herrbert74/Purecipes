package com.purecipes.feature.search.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import com.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RecipeSearchScreenTest {

	@Test
	fun searchScreenTypingDoesNotRecomposeResults() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository()
		val filterRepository = FakeRecipeSearchFilterRepository()
		val settingsRepository = FakeMeasurementPreferencesRepository()
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
					getMeasurementPreferences = GetMeasurementPreferencesUseCase(settingsRepository),
					searchRecipes = SearchRecipesUseCase(searchRepository),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
					getSearchFilters = GetSearchFiltersUseCase(filterRepository),
					saveSearchFilters = SaveSearchFiltersUseCase(filterRepository),
				)
			}
		}
		onNodeWithText("Search recipes").assertIsDisplayed()
		onNodeWithTag(RECIPE_SEARCH_INPUT_TAG).performTextInput("Pas")
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).assertStable()
	}
}

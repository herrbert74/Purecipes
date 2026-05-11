package app.purecipes.feature.search.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Err
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
		val pantryRepository = FakeUserPantryRepository()
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
					getUserPantry = GetUserPantryUseCase(pantryRepository),
					updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
				)
			}
		}
		onNodeWithText("Search recipes").assertIsDisplayed()
		onNodeWithTag(RECIPE_SEARCH_INPUT_TAG).performTextInput("Pas")
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).assertStable()
	}

	@Test
	fun searchScreenShowsTotalMatchesCount() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository(
			result = com.github.michaelbull.result.Ok(
				listOf(
					RecipeSummary(
						id = 1,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
					),
				),
			),
		)
		val filterRepository = FakeRecipeSearchFilterRepository()
		val pantryRepository = FakeUserPantryRepository()
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
					getUserPantry = GetUserPantryUseCase(pantryRepository),
					updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
				)
			}
		}

		onNodeWithText("1 recipes found").assertIsDisplayed()
	}

	@Test
	fun searchScreenShowsErrorFromSearchFailure() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository(
			result = Err(Failure.ServerError("Search failed")),
		)
		val filterRepository = FakeRecipeSearchFilterRepository()
		val pantryRepository = FakeUserPantryRepository()
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
					getUserPantry = GetUserPantryUseCase(pantryRepository),
					updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
				)
			}
		}

		onNodeWithText("Search failed").assertIsDisplayed()
	}
}

package app.purecipes.feature.search.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_PANTRY_TAB_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_RECIPE_FILTERS_INTRO_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_SIGN_IN_PROMPT_TITLE_TAG
import app.purecipes.feature.search.ui.filter.FILTER_PANTRY_BULK_CLEAR_ALL_TAG
import app.purecipes.feature.search.ui.filter.FILTER_PANTRY_BULK_SELECT_ALL_TAG
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
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RecipeSearchScreenTest {

	@Test
	fun searchScreenTypingDoesNotRecomposeResults() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository()
		val viewModel = recipeSearchViewModelForTest(searchRepository = searchRepository)
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					viewModel = viewModel,
				)
			}
		}
		waitUntil(timeoutMillis = 5_000) { searchRepository.queries.isNotEmpty() }
		onNodeWithTag(RECIPE_SEARCH_COLLAPSED_BAR_TAG).assertIsDisplayed()
		onNodeWithTag(RECIPE_SEARCH_COLLAPSED_BAR_TAG).performClick()
		runOnIdle { viewModel.onSearchQueryChange("Pas") }
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

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					viewModel = recipeSearchViewModelForTest(searchRepository = searchRepository),
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

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					viewModel = recipeSearchViewModelForTest(searchRepository = searchRepository),
				)
			}
		}

		onNodeWithText("Search failed").assertIsDisplayed()
	}

	@Test
	fun whenSignedOutOpeningFiltersShowsSignInPrompt() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = false,
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_SIGN_IN_PROMPT_TITLE_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedOutGoToAccountDismissesSheetAndInvokesCallback() = runRecompositionTrackingUiTest {
		var loginRequestCount = 0
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = false,
					onRequestLogInForFilters = { loginRequestCount++ },
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG).performClick()
		waitForIdle()

		assertEquals(1, loginRequestCount)
		onAllNodesWithTag(FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG).assertCountEquals(0)
	}

	@Test
	fun whenSignedInOpeningFiltersShowsPantryTabByDefault() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_PANTRY_TAB_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedInSwitchingToRecipeFiltersTabShowsRecipeFiltersIntro() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_INTRO_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedInOpeningPantryTabShowsBulkActionChips() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_PANTRY_BULK_SELECT_ALL_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_PANTRY_BULK_CLEAR_ALL_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedInExpandingPantryGroupShowsBulkActionChips() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = recipeSearchViewModelForTest(),
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithText("Poultry & Eggs").performClick()
		waitForIdle()
		onAllNodesWithText("Select all").assertCountEquals(2)
		onAllNodesWithText("Clear all").assertCountEquals(2)
	}
}

private fun recipeSearchViewModelForTest(
	searchRepository: FakeRecipeSearchRepository = FakeRecipeSearchRepository(),
	filterRepository: FakeRecipeSearchFilterRepository = FakeRecipeSearchFilterRepository(),
	pantryRepository: FakeUserPantryRepository = FakeUserPantryRepository(),
	settingsRepository: FakeMeasurementPreferencesRepository = FakeMeasurementPreferencesRepository(),
	initialShowFilterSheet: Boolean = false,
): RecipeSearchViewModel = RecipeSearchViewModel(
	filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
	getMeasurementPreferences = GetMeasurementPreferencesUseCase(settingsRepository),
	searchRecipes = SearchRecipesUseCase(searchRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	getSearchFilters = GetSearchFiltersUseCase(filterRepository),
	saveSearchFilters = SaveSearchFiltersUseCase(filterRepository),
	getUserPantry = GetUserPantryUseCase(pantryRepository),
	updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
	searchReadiness = SearchReadinessCoordinator(),
	initialShowFilterSheet = initialShowFilterSheet,
	sessionKey = null,
)

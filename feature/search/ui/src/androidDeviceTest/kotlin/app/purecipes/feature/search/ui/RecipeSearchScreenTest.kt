package app.purecipes.feature.search.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.ObserveSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.filter.FILTER_ADD_INGREDIENT_BUTTON_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_GO_TO_ACCOUNT_BUTTON_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_PANTRY_TAB_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_RECIPE_FILTERS_INTRO_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_SCROLL_TAG
import app.purecipes.feature.search.ui.filter.FILTER_BOTTOM_SHEET_SIGN_IN_PROMPT_TITLE_TAG
import app.purecipes.feature.search.ui.filter.FILTER_INGREDIENT_LEGEND_EXCLUDED_TAG
import app.purecipes.feature.search.ui.filter.FILTER_INGREDIENT_LEGEND_NEUTRAL_TAG
import app.purecipes.feature.search.ui.filter.FILTER_INGREDIENT_LEGEND_PANTRY_TAG
import app.purecipes.feature.search.ui.filter.FILTER_KEY_INGREDIENTS_EMPTY_PANTRY_TAG
import app.purecipes.feature.search.ui.filter.FILTER_KEY_INGREDIENTS_GO_TO_PANTRY_TAG
import app.purecipes.feature.search.ui.filter.FILTER_KEY_INGREDIENTS_SECTION_TAG
import app.purecipes.feature.search.ui.filter.FILTER_PANTRY_BULK_SELECT_ALL_TAG
import app.purecipes.feature.search.ui.filter.customIngredientChipTag
import app.purecipes.feature.search.ui.filter.customIngredientRemoveTag
import app.purecipes.feature.search.ui.filter.filterRecipeClearAllTag
import app.purecipes.feature.search.ui.filter.filterSectionToggleTag
import app.purecipes.feature.search.ui.filter.ingredientTriStateChipTag
import app.purecipes.feature.search.ui.filter.keyIngredientChipTag
import app.purecipes.feature.search.ui.filter.keyIngredientPantryQuickPickTag
import app.purecipes.feature.search.ui.result.SEARCH_RESULTS_LIST_TAG
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeIngredientMatchRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.ui.component.RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX
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
	fun searchScreenShowsPantryFilterNoteWhenSignedIn() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest(
			pantryRepository = FakeUserPantryRepository(setOf("Chicken")),
			sessionKey = "session",
		)
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					sessionKey = "session",
					viewModel = viewModel,
				)
			}
		}

		onNodeWithTag(SEARCH_FILTER_NOTE_TAG).assertIsDisplayed()
		onNodeWithText("Showing recipes you can make with your pantry.").assertIsDisplayed()
	}

	@Test
	fun searchScreenShowsFavoriteHeartOnFavoritedRecipe() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository(
			result = com.github.michaelbull.result.Ok(
				listOf(
					RecipeSummary(
						id = 1,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
						isFavorite = true,
					),
					RecipeSummary(
						id = 2,
						title = "Green Salad",
						cuisine = Cuisine.FRENCH,
						imageUrl = null,
						totalTime = 15,
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

		onNodeWithTag("${RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX}1").assertIsDisplayed()
		onNodeWithTag("${RECIPE_CARD_FAVORITE_ICON_TAG_PREFIX}2").assertDoesNotExist()
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
	fun searchScreenShowsNearMissRecipesWhenSearchIsEmpty() = runRecompositionTrackingUiTest {
		val searchRepository = FakeRecipeSearchRepository(
			result = com.github.michaelbull.result.Ok(emptyList()),
			totalMatches = 0,
			nearMissRecipes = listOf(
				NearMissRecipe(
					recipe = RecipeSummary(
						id = 9,
						title = "Almost Stew",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 30,
					),
					missingIngredient = "Basil",
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

		onNodeWithText("0 recipes found").assertIsDisplayed()
		onNodeWithText("Do you have Basil?").assertIsDisplayed()
		onNodeWithText("Almost Stew").assertIsDisplayed()
	}

	@Test
	fun searchScreenShowsNearMissRecipesWhenSearchReturnsFewResults() = runRecompositionTrackingUiTest {
		val mainRecipes = listOf(
			RecipeSummary(
				id = 1,
				title = "Chicken Tomato Stew",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 30,
			),
			RecipeSummary(
				id = 2,
				title = "Simple Salad",
				cuisine = Cuisine.FRENCH,
				imageUrl = null,
				totalTime = 10,
			),
			RecipeSummary(
				id = 3,
				title = "Garlic Bread",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 20,
			),
		)
		val searchRepository = FakeRecipeSearchRepository(
			result = com.github.michaelbull.result.Ok(mainRecipes),
			totalMatches = mainRecipes.size,
			nearMissRecipes = listOf(
				NearMissRecipe(
					recipe = RecipeSummary(
						id = 9,
						title = "Almost Stew",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 35,
					),
					missingIngredient = "Basil",
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

		onNodeWithText("3 recipes found").assertIsDisplayed()
		onNodeWithText("Chicken Tomato Stew").assertIsDisplayed()
		onNodeWithTag(SEARCH_RESULTS_LIST_TAG)
			.performScrollToNode(hasText("Almost Stew"))
		onNodeWithText("Almost Stew").assertIsDisplayed()
		onNodeWithTag(SEARCH_RESULTS_LIST_TAG)
			.performScrollToNode(hasText("Do you have Basil?"))
		onNodeWithText("Do you have Basil?").assertIsDisplayed()
		onNodeWithTag(SEARCH_RESULTS_LIST_TAG)
			.performScrollToNode(
				hasText("These are almost a match — you're only missing one ingredient."),
			)
		onNodeWithText(
			"These are almost a match — you're only missing one ingredient.",
		).assertIsDisplayed()
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
	fun whenSignedInRecipeFiltersTabHasNoSelectAllActions() = runRecompositionTrackingUiTest {
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
		onAllNodesWithText("Select all").assertCountEquals(0)
	}

	@Test
	fun whenSignedInOpeningPantryTabShowsSelectAllChipOnlyWhenPantryEmpty() = runRecompositionTrackingUiTest {
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
		onAllNodesWithText("Clear all").assertCountEquals(0)
	}

	@Test
	fun whenSignedInRecipeFiltersSectionShowsClearActionWhenTwoOrMoreSelected() = runRecompositionTrackingUiTest {
		val cuisineSectionTag = filterSectionToggleTag("Cuisine")
		val cuisineClearAllTag = filterRecipeClearAllTag("Cuisine")
		val viewModel = recipeSearchViewModelForTest()

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).performClick()
		waitForIdle()
		onNodeWithTag(cuisineSectionTag).performClick()
		waitForIdle()
		runOnIdle {
			viewModel.onFiltersChange(SearchFilters(cuisines = setOf(Cuisine.ITALIAN)))
		}
		waitForIdle()
		onAllNodesWithTag(cuisineClearAllTag).assertCountEquals(0)
		runOnIdle {
			viewModel.onFiltersChange(
				SearchFilters(cuisines = setOf(Cuisine.ITALIAN, Cuisine.FRENCH)),
			)
		}
		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithTag(cuisineClearAllTag).fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithTag(cuisineClearAllTag).assertIsDisplayed()
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
		onAllNodesWithText("Clear all").assertCountEquals(0)
	}

	@Test
	fun whenSignedInPantryTabShowsIngredientLegendAndExclusionIntro() = runRecompositionTrackingUiTest {
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
		onNodeWithTag(FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_INGREDIENT_LEGEND_NEUTRAL_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_INGREDIENT_LEGEND_PANTRY_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_INGREDIENT_LEGEND_EXCLUDED_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedInIngredientChipCyclesThroughPantryAndExcludedStates() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest()

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithText("Poultry & Eggs").performClick()
		waitForIdle()
		onNodeWithTag(ingredientTriStateChipTag("Chicken")).performClick()
		waitForIdle()
		runOnIdle {
			assertEquals(setOf("Chicken"), viewModel.pantryIngredients)
			assertEquals(emptySet<String>(), viewModel.excludedIngredients)
		}
		onNodeWithTag(ingredientTriStateChipTag("Chicken")).performClick()
		waitForIdle()
		runOnIdle {
			assertEquals(emptySet<String>(), viewModel.pantryIngredients)
			assertEquals(setOf("Chicken"), viewModel.excludedIngredients)
		}
	}

	@Test
	fun whenSignedInYourIngredientsSectionShowsAddIngredientButton() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest()

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_SCROLL_TAG)
			.performScrollToNode(hasText("Your ingredients"))
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_SCROLL_TAG)
			.performScrollToNode(hasText("Add ingredient"))
		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithTag(FILTER_ADD_INGREDIENT_BUTTON_TAG).fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithTag(FILTER_ADD_INGREDIENT_BUTTON_TAG).assertIsDisplayed()
	}

	@Test
	fun whenSignedInCustomIngredientStaysVisibleUntilRemoved() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest()

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		runOnIdle { viewModel.onAddIngredient("gochujang") }
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_SCROLL_TAG)
			.performScrollToNode(hasTestTag(customIngredientChipTag("gochujang")))
		onNodeWithTag(customIngredientChipTag("gochujang")).performClick()
		waitForIdle()
		onNodeWithTag(customIngredientChipTag("gochujang")).performClick()
		waitForIdle()
		onNodeWithTag(customIngredientChipTag("gochujang")).assertIsDisplayed()
		onNodeWithTag(customIngredientRemoveTag("gochujang"), useUnmergedTree = true).performClick()
		waitUntil(timeoutMillis = 5_000) {
			viewModel.customPantryIngredients.isEmpty()
		}
		onAllNodesWithText("gochujang").assertCountEquals(0)
		runOnIdle {
			assertEquals(emptySet<String>(), viewModel.pantryIngredients)
		}
	}

	@Test
	fun whenSignedInRecipeFiltersTabShowsKeyIngredientsSection() = runRecompositionTrackingUiTest {
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
		onNodeWithTag(FILTER_KEY_INGREDIENTS_SECTION_TAG).assertIsDisplayed()
		onNodeWithContentDescription("Key ingredients is a premium filter").assertIsDisplayed()
	}

	@Test
	fun whenFreeUserTapsLockedKeyIngredientsOpensPaywall() = runRecompositionTrackingUiTest {
		var openedPaywall = false
		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = recipeSearchViewModelForTest(),
					onOpenPaywall = { _ -> openedPaywall = true },
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).performClick()
		waitForIdle()
		onNodeWithTag(filterSectionToggleTag("Key ingredients")).performClick()
		waitForIdle()
		runOnIdle {
			assertEquals(true, openedPaywall)
		}
	}

	@Test
	fun whenPremiumPantryQuickPickAddsKeyIngredient() = runRecompositionTrackingUiTest {
		val pantryRepository = FakeUserPantryRepository(pantry = setOf("Chicken", "Rice"))
		val viewModel = recipeSearchViewModelForTest(
			pantryRepository = pantryRepository,
			isPremium = true,
		)

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					sessionKey = "signed-in",
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).performClick()
		waitForIdle()
		onNodeWithTag(filterSectionToggleTag("Key ingredients")).performClick()
		waitForIdle()
		onNodeWithTag(keyIngredientPantryQuickPickTag("Chicken")).performClick()
		waitForIdle()
		runOnIdle {
			assertEquals(setOf("Chicken"), viewModel.keyIngredients)
		}
		onNodeWithTag(keyIngredientChipTag("Chicken")).assertIsDisplayed()
	}

	@Test
	fun whenPremiumEmptyPantryShowsCalloutAndGoToPantrySwitchesTab() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest(isPremium = true)

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					sessionKey = "signed-in",
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_RECIPE_FILTERS_TAB_TAG).performClick()
		waitForIdle()
		onNodeWithTag(filterSectionToggleTag("Key ingredients")).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_KEY_INGREDIENTS_EMPTY_PANTRY_TAG).assertIsDisplayed()
		onNodeWithTag(FILTER_KEY_INGREDIENTS_GO_TO_PANTRY_TAG).performClick()
		waitForIdle()
		onNodeWithTag(FILTER_BOTTOM_SHEET_PANTRY_INTRO_TAG).assertIsDisplayed()
	}

	@Test
	fun whenKeyIngredientsSelectedFilterButtonShowsActiveState() = runRecompositionTrackingUiTest {
		val viewModel = recipeSearchViewModelForTest(isPremium = true)

		setTrackedContent {
			PurecipesTheme {
				RecipeSearchScreen(
					isSignedIn = true,
					viewModel = viewModel,
				)
			}
		}

		waitForIdle()
		runOnIdle { viewModel.onKeyIngredientsChange(setOf("Tomato")) }
		waitForIdle()
		onNodeWithTag(RECIPE_SEARCH_OPEN_FILTERS_BUTTON_TAG).assertIsDisplayed()
	}

}

private fun recipeSearchViewModelForTest(
	searchRepository: FakeRecipeSearchRepository = FakeRecipeSearchRepository(),
	filterRepository: FakeRecipeSearchFilterRepository = FakeRecipeSearchFilterRepository(),
	pantryRepository: FakeUserPantryRepository = FakeUserPantryRepository(),
	excludedIngredientsRepository: FakeUserExcludedIngredientsRepository = FakeUserExcludedIngredientsRepository(),
	settingsRepository: FakeMeasurementPreferencesRepository = FakeMeasurementPreferencesRepository(),
	ingredientMatchRepository: FakeIngredientMatchRepository = FakeIngredientMatchRepository(),
	initialShowFilterSheet: Boolean = false,
	isPremium: Boolean = false,
	sessionKey: String? = null,
): RecipeSearchViewModel {
	val searchPreferencesRepository = FakeSearchPreferencesRepository()
	return RecipeSearchViewModel(
		filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
		getMeasurementPreferences = GetMeasurementPreferencesUseCase(settingsRepository),
		searchRecipes = SearchRecipesUseCase(searchRepository),
		trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
		logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
		sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
		getSearchFilters = GetSearchFiltersUseCase(filterRepository),
		saveSearchFilters = SaveSearchFiltersUseCase(filterRepository),
		getSearchPreferences = GetSearchPreferencesUseCase(searchPreferencesRepository),
		observeSearchPreferences = ObserveSearchPreferencesUseCase(searchPreferencesRepository),
		getUserPantry = GetUserPantryUseCase(pantryRepository),
		updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
		getUserExcludedIngredients = GetUserExcludedIngredientsUseCase(excludedIngredientsRepository),
		updateUserExcludedIngredients = UpdateUserExcludedIngredientsUseCase(excludedIngredientsRepository),
		matchIngredientInRecipes = MatchIngredientInRecipesUseCase(ingredientMatchRepository),
		searchReadiness = SearchReadinessCoordinator(),
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		observePremiumStatus = ObservePremiumStatusUseCase(
			FakeSubscriptionRepository(
				initialState = if (isPremium) {
					SubscriptionState(
						status = SubscriptionStatus.PREMIUM,
						isActive = true,
						expirationInstant = null,
						trialActive = false,
					)
				} else {
					SubscriptionState.FREE
				},
			),
			FakeMonetisationDebugOverridesRepository(),
		),
		initialShowFilterSheet = initialShowFilterSheet,
		sessionKey = sessionKey,
	)
}

package app.purecipes.feature.cooking.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class StepByStepCookingRouteTest {

	@Test
	fun cookingRouteShowsRecipeTitleProgressAndSwipeableSteps() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				StepByStepCookingRoute(
					recipeId = 9,
					canManageFavorites = true,
					onBack = {},
					onFindMoreRecipes = {},
					viewModel = stepByStepCookingViewModelForTest(
						recipeId = 9,
						recipeDetailsRepository = roastedCarrotsRepository(),
					),
				)
			}
		}

		onNodeWithText("Roasted Carrots").assertIsDisplayed()
		onNodeWithText("1 of 2").assertIsDisplayed()
		onNodeWithText("Trim the carrots").assertIsDisplayed()

		onNodeWithText("Trim the carrots").performTouchInput {
			swipeLeft()
		}

		onNodeWithText("2 of 2").assertIsDisplayed()
		onNodeWithText("Roast until tender").assertIsDisplayed()
		onNodeWithText("Finish cooking").assertIsDisplayed()
		onNodeWithTag(STEP_BY_STEP_CURRENT_STEP_TEXT_TAG).assertStable()
	}

	@Test
	fun cookingRouteShowsFinishActionsAfterLastStep() = runRecompositionTrackingUiTest {
		var doneClicked = false
		var findMoreClicked = false
		setTrackedContent {
			PurecipesTheme {
				StepByStepCookingRoute(
					recipeId = 9,
					canManageFavorites = true,
					onBack = { doneClicked = true },
					onFindMoreRecipes = { findMoreClicked = true },
					viewModel = stepByStepCookingViewModelForTest(
						recipeId = 9,
						recipeDetailsRepository = roastedCarrotsRepository(),
					),
				)
			}
		}

		onNodeWithText("Trim the carrots").performTouchInput {
			swipeLeft()
		}
		onNodeWithText("Finish cooking").performClick()

		onNodeWithTag(COOKING_FINISHED_CONTENT_TAG).assertIsDisplayed()
		onNodeWithText("Enjoy your meal").assertIsDisplayed()
		onNodeWithText("Favorite").assertIsDisplayed()
		onNodeWithText("Share").assertIsDisplayed()
		onAllNodesWithText("1 of 2").assertCountEquals(0)
		onAllNodesWithTag(COOKING_ADD_TO_COOKBOOK_BUTTON_TAG).assertCountEquals(0)

		onNodeWithText("Done").performClick()
		assertTrue(doneClicked)

		onNodeWithText("Find more recipes").performClick()
		assertTrue(findMoreClicked)
	}

	@Test
	fun cookingRouteShowsAddToCookbookWhenRecipeIsFavorite() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				StepByStepCookingRoute(
					recipeId = 9,
					canManageFavorites = true,
					onBack = {},
					onFindMoreRecipes = {},
					viewModel = stepByStepCookingViewModelForTest(
						recipeId = 9,
						recipeDetailsRepository = roastedCarrotsRepository(isFavorite = true),
						cookbooksRepository = FakeCookbooksRepository(
							cookbooksPageResult = Ok(
								CookbookListPage(
									items = listOf(
										CookbookSummary(
											id = 8,
											name = "Sunday Roast",
											recipeCount = 2,
											updatedAtEpochMillis = 0L,
										),
									),
									pageNumber = 1,
									pageSize = 20,
									totalMatches = 1,
								),
							),
						),
					),
				)
			}
		}

		onNodeWithText("Trim the carrots").performTouchInput {
			swipeLeft()
		}
		onNodeWithText("Finish cooking").performClick()

		onNodeWithTag(COOKING_ADD_TO_COOKBOOK_BUTTON_TAG).assertIsDisplayed()
		onNodeWithTag(COOKING_ADD_TO_COOKBOOK_BUTTON_TAG).performClick()
		waitForIdle()

		onNodeWithText("Create and add").assertIsDisplayed()
		onNodeWithText("Sunday Roast").assertIsDisplayed()
	}

	@Test
	fun cookingRouteHidesAddToCookbookWhenSignedOut() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				StepByStepCookingRoute(
					recipeId = 9,
					canManageFavorites = false,
					onBack = {},
					onFindMoreRecipes = {},
					viewModel = stepByStepCookingViewModelForTest(
						recipeId = 9,
						recipeDetailsRepository = roastedCarrotsRepository(isFavorite = true),
					),
				)
			}
		}

		onNodeWithText("Trim the carrots").performTouchInput {
			swipeLeft()
		}
		onNodeWithText("Finish cooking").performClick()

		onAllNodesWithTag(COOKING_ADD_TO_COOKBOOK_BUTTON_TAG).assertCountEquals(0)
	}
}

private fun roastedCarrotsRepository(
	isFavorite: Boolean = false,
): FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(
	fakeRecipeDetails(
		id = 9,
		title = "Roasted Carrots",
		description = "Sweet and savory side dish.",
		imageUrl = null,
		ingredientGroups = listOf(
			IngredientGroup(
				name = "Ingredients",
				ingredients = recipeIngredients("6 carrots", "2 tbsp olive oil"),
			),
		),
		steps = listOf("Trim the carrots", "Roast until tender"),
		totalTime = 35,
		yields = "4 servings",
		cuisine = Cuisine.MEDITERRANEAN,
	).copy(isFavorite = isFavorite),
)

private fun stepByStepCookingViewModelForTest(
	recipeId: Int,
	recipeDetailsRepository: FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(fakeRecipeDetails()),
	cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
): StepByStepCookingViewModel {
	val favoritesRepository = FakeFavoritesRepository()
	return StepByStepCookingViewModel(
		addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
		addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
		createCookbook = CreateCookbookUseCase(cookbooksRepository),
		getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
		getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
		observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(
			FakeMeasurementPreferencesRepository(),
		),
		processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
		removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
		shareRecipe = ShareRecipeUseCase(
			object : ShareRepository {
				override fun shareText(text: String, title: String?) = Unit
			},
		),
		trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
		logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
		sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
		recipeId = recipeId,
		sessionKey = null,
	)
}

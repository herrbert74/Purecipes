package app.purecipes.feature.cooking.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class StepByStepCookingRouteTest {

	@Test
	fun cookingRouteShowsRecipeTitleProgressAndSwipeableSteps() = runRecompositionTrackingUiTest {
		val measurementRepository = FakeMeasurementPreferencesRepository()
		setTrackedContent {
			PurecipesTheme {
				StepByStepCookingRoute(
					recipeId = 9,
					onBack = {},
					viewModel = stepByStepCookingViewModelForTest(
						recipeId = 9,
						measurementRepository = measurementRepository,
						recipeDetailsRepository = FakeRecipeDetailsRepository(
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
							),
						),
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
		onNodeWithTag(STEP_BY_STEP_CURRENT_STEP_TEXT_TAG).assertStable()
	}
}

private fun stepByStepCookingViewModelForTest(
	recipeId: Int,
	measurementRepository: FakeMeasurementPreferencesRepository = FakeMeasurementPreferencesRepository(),
	recipeDetailsRepository: FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(fakeRecipeDetails()),
): StepByStepCookingViewModel = StepByStepCookingViewModel(
	getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
	observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
	processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	recipeId = recipeId,
)

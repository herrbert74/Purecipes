package com.purecipes.feature.cooking.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import com.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
					getRecipeDetails = GetRecipeDetailsUseCase(
						FakeRecipeDetailsRepository(
							RecipeDetails(
								id = 9,
								title = "Roasted Carrots",
								description = "Sweet and savory side dish.",
								imageUrl = null,
								ingredientGroups = listOf(
									IngredientGroup(
										name = "Ingredients",
										ingredients = listOf("6 carrots", "2 tbsp olive oil"),
									),
								),
								steps = listOf("Trim the carrots", "Roast until tender"),
								totalTime = 35,
								yields = "4 servings",
								cuisine = Cuisine.MEDITERRANEAN,
							),
						),
					),
					getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
					processRecipeDetailsForMeasurementPreferences =
						ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
					onBack = {},
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

	private class FakeMeasurementPreferencesRepository : MeasurementPreferencesRepository {

		private val flow = MutableStateFlow(
			MeasurementPreferences(
				preferredSystem = MeasurementSystem.METRIC,
			),
		)

		override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = flow

		override suspend fun getMeasurementPreferences(): MeasurementPreferences = flow.value

		override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
			flow.value = preferences
		}

		override suspend fun resetMeasurementPreferences() = Unit

		override suspend fun markMismatchNotificationSeen(recipeId: Int) = Unit
	}

}

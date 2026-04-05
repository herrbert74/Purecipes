package com.purecipes.feature.cooking.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import org.junit.Rule
import org.junit.Test

class StepByStepCookingRouteTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun cookingRouteShowsRecipeTitleProgressAndSwipeableSteps() {
		composeRule.setContent {
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
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				onBack = {},
			)
		}

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("1 of 2").assertIsDisplayed()
		composeRule.onNodeWithText("Trim the carrots").assertIsDisplayed()

		composeRule.onNodeWithText("Trim the carrots").performTouchInput {
			swipeLeft()
		}

		composeRule.onNodeWithText("2 of 2").assertIsDisplayed()
		composeRule.onNodeWithText("Roast until tender").assertIsDisplayed()
	}

}

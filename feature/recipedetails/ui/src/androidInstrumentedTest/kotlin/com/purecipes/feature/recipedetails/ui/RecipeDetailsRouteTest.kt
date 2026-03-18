package com.purecipes.feature.recipedetails.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import org.junit.Rule
import org.junit.Test

class RecipeDetailsRouteTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun recipe_details_route_shows_title_ingredients_and_steps() {
		composeRule.setContent {
			RecipeDetailsRoute(
				recipeId = 7,
				repository = FakeRecipeDetailsRepository(
					RecipeDetails(
						id = 7,
						title = "Roasted Carrots",
						description = "Sweet and savory side dish.",
						imageUrl = null,
						ingredientGroups = listOf(
							IngredientGroup(
								name = "Ingredients",
								ingredients = listOf("6 carrots", "2 tbsp olive oil"),
							)
						),
						steps = listOf("Trim the carrots", "Roast until tender"),
						totalTime = 35,
						yields = "4 servings",
						cuisine = "Mediterranean",
					)
				),
				onBack = {},
				onStartCooking = {},
			)
		}

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("Sweet and savory side dish.").assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()
		composeRule.onNodeWithText("- 6 carrots").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText("Roast until tender").performScrollTo().assertIsDisplayed()
	}

	private class FakeRecipeDetailsRepository(
		private val recipeDetails: RecipeDetails,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> {
			return Ok(recipeDetails)
		}
	}
}

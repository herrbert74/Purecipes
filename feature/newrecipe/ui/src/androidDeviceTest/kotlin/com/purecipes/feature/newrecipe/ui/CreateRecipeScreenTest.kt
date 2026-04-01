package com.purecipes.feature.newrecipe.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import org.junit.Rule
import org.junit.Test

class CreateRecipeScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun createRecipeScreenSavesAndDisplaysRecipe() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			)
		}

		composeRule.onNodeWithTag("createRecipeTitleField").performTextInput("Roasted Carrots")
		composeRule.onNodeWithTag("createRecipeDescriptionField").performTextInput("Sweet and savory side dish.")
		composeRule.onNodeWithTag("createRecipeStepsField").performTextInput("Trim the carrots\nRoast until tender")
		composeRule.onNodeWithTag("createRecipeSaveButton").performClick()

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("Recipe uploaded.").assertIsDisplayed()
		composeRule.onNodeWithText("Edit").assertIsDisplayed()
	}

	private class FakeCreatedRecipeRepository : CreatedRecipeRepository {

		private val recipes = mutableListOf<RecipeDetails>()

		override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> = Ok(recipes.toList())

		override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
			val recipe = RecipeDetails(
				id = request.recipeId ?: -1,
				title = request.title,
				description = request.description,
				imageUrl = request.imageUrl,
				ingredientGroups = listOf(IngredientGroup(ingredients = request.ingredients)),
				steps = request.steps,
				totalTime = request.totalTime,
				yields = request.yields,
				cuisine = request.cuisine,
			)
			recipes.removeAll { it.id == recipe.id }
			recipes.add(index = 0, element = recipe)
			return Ok(recipe)
		}
	}
}

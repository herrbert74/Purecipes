package com.purecipes.feature.newrecipe.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import org.junit.Rule
import org.junit.Test

private const val STEP_REORDER_DRAG_DISTANCE = -140f
private const val STEP_REORDER_LONG_PRESS_MILLIS = 700L

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
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		}

		composeRule.onNodeWithTag("createRecipeTitleField").performTextInput("Roasted Carrots")
		composeRule.onNodeWithTag("createRecipeDescriptionField").performTextInput("Sweet and savory side dish.")
		composeRule.onNodeWithTag("createRecipeImagePickButton").assertIsDisplayed()
		composeRule.onNodeWithTag("createRecipeStepField0").performTextInput("Trim the carrots")
		composeRule.onNodeWithTag("createRecipeAddStepButton").performClick()
		composeRule.onNodeWithTag("createRecipeStepField1").performTextInput("Roast until tender")
		composeRule.onNodeWithTag("createRecipeSaveButton").performClick()

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("Recipe uploaded.").assertIsDisplayed()
		composeRule.onNodeWithText("Edit").assertIsDisplayed()
	}

	@Test
	fun createRecipeScreenDisablesFormWhileImageImportIsInProgress() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				rememberImagePicker = { _, onImportStateChange, _ ->
					object : RecipeImagePickerLauncher {
						override fun launch() {
							onImportStateChange(true)
						}
					}
				},
			)
		}

		composeRule.onNodeWithTag("createRecipeImagePickButton").performClick()

		composeRule.onNodeWithText("Importing image").assertIsDisplayed()
		composeRule.onNodeWithText("Preparing image preview...").assertIsDisplayed()
		composeRule.onNodeWithTag("createRecipeImagePickButton").assertIsNotEnabled()
		composeRule.onNodeWithTag("createRecipeSaveButton").assertIsNotEnabled()
	}

	@Test
	fun createRecipeScreenShowsImageImportErrorAndReEnablesForm() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				rememberImagePicker = { _, onImportStateChange, onPickerError ->
					object : RecipeImagePickerLauncher {
						override fun launch() {
							onImportStateChange(true)
							onPickerError("Could not import the selected image.")
						}
					}
				},
			)
		}

		composeRule.onNodeWithTag("createRecipeImagePickButton").performClick()

		composeRule.onNodeWithText("Could not import the selected image.").assertIsDisplayed()
		composeRule.onNodeWithTag("createRecipeImagePickButton").assertIsEnabled()
		composeRule.onNodeWithTag("createRecipeSaveButton").assertIsEnabled()
	}

	@Test
	fun createRecipeScreenOpensCuisinePicker() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		}

		composeRule.onNodeWithTag("createRecipeCuisineField").performClick()

		composeRule.onNodeWithText("No cuisine").assertIsDisplayed()
		composeRule.onNodeWithText("Italian").assertIsDisplayed()
	}

	@Test
	fun createRecipeScreenAddsAnotherStepField() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		}

		composeRule.onNodeWithTag("createRecipeAddStepButton").performClick()

		composeRule.onNodeWithTag("createRecipeStepField1").assertIsDisplayed()
	}

	@Test
	fun createRecipeScreenReordersSteps() {
		val repository = FakeCreatedRecipeRepository()
		composeRule.setContent {
			CreateRecipeScreen(
				canUploadRecipes = true,
				getCreatedRecipes = GetCreatedRecipesUseCase(repository),
				saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		}

		composeRule.onNodeWithTag("createRecipeStepField0").performTextInput("First")
		composeRule.onNodeWithTag("createRecipeAddStepButton").performClick()
		composeRule.onNodeWithTag("createRecipeStepField1").performTextInput("Second")
		composeRule.onNodeWithTag("createRecipeReorderStepButton1").performTouchInput {
			down(center)
			advanceEventTime(STEP_REORDER_LONG_PRESS_MILLIS)
			moveBy(Offset(x = 0f, y = STEP_REORDER_DRAG_DISTANCE))
			up()
		}
		composeRule.waitForIdle()

		composeRule.onNodeWithTag("createRecipeStepField0").assertTextContains("Second")
		composeRule.onNodeWithTag("createRecipeStepField1").assertTextContains("First")
	}

}

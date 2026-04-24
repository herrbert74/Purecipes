package com.purecipes.feature.newrecipe.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import com.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

private const val STEP_REORDER_DRAG_DISTANCE = -140f
private const val STEP_REORDER_LONG_PRESS_MILLIS = 700L

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class CreateRecipeScreenTest {

	@Test
	fun createRecipeScreenSavesAndDisplaysRecipe() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					getCreatedRecipes = GetCreatedRecipesUseCase(repository),
					saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				)
			}
		}

		onNodeWithTag("createRecipeTitleField").performTextInput("Roasted Carrots")
		onNodeWithTag("createRecipeDescriptionField").performTextInput("Sweet and savory side dish.")
		onNodeWithTag("createRecipeImagePickButton").assertIsDisplayed()
		onNodeWithTag("createRecipeStepField0").performTextInput("Trim the carrots")
		onNodeWithTag("createRecipeAddStepButton").performClick()
		onNodeWithTag("createRecipeStepField1").performTextInput("Roast until tender")
		onNodeWithTag("createRecipeSaveButton").performScrollTo().performClick()
		waitForIdle()
		waitUntil(timeoutMillis = 5_000) { repository.savedRequests.size == 1 }

		onNodeWithText("Recipe uploaded.").performScrollTo().assertIsDisplayed()
		assertEquals(1, repository.savedRequests.size)
		onNodeWithTag("createRecipeSaveButton").assertStable()
	}

	@Test
	fun createRecipeScreenDisablesFormWhileImageImportIsInProgress() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
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
		}

		onNodeWithTag("createRecipeImagePickButton").performClick()

		onNodeWithText("Importing image").assertIsDisplayed()
		onNodeWithText("Preparing image preview...").assertIsDisplayed()
		onNodeWithTag("createRecipeImagePickButton").assertIsNotEnabled()
		onNodeWithTag("createRecipeSaveButton").assertIsNotEnabled()
	}

	@Test
	fun createRecipeScreenShowsImageImportErrorAndReEnablesForm() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
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
		}

		onNodeWithTag("createRecipeImagePickButton").performClick()

		onNodeWithText("Could not import the selected image.").assertIsDisplayed()
		onNodeWithTag("createRecipeImagePickButton").assertIsEnabled()
		onNodeWithTag("createRecipeSaveButton").assertIsEnabled()
	}

	@Test
	fun createRecipeScreenOpensCuisinePicker() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					getCreatedRecipes = GetCreatedRecipesUseCase(repository),
					saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				)
			}
		}

		onNodeWithTag("createRecipeCuisineField").performClick()

		onNodeWithText("No cuisine").assertIsDisplayed()
		onNodeWithText("Italian").assertIsDisplayed()
	}

	@Test
	fun createRecipeScreenAddsAnotherStepField() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					getCreatedRecipes = GetCreatedRecipesUseCase(repository),
					saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				)
			}
		}

		onNodeWithTag("createRecipeAddStepButton").performClick()
		waitForIdle()

		onNodeWithTag("createRecipeStepField1").performScrollTo().assertIsDisplayed()
	}

	@Test
	fun createRecipeScreenReordersSteps() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					getCreatedRecipes = GetCreatedRecipesUseCase(repository),
					saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
				)
			}
		}

		onNodeWithTag("createRecipeStepField0").performTextInput("First")
		onNodeWithTag("createRecipeAddStepButton").performClick()
		onNodeWithTag("createRecipeStepField1").performTextInput("Second")
		onNodeWithTag("createRecipeReorderStepButton1").performTouchInput {
			down(center)
			advanceEventTime(STEP_REORDER_LONG_PRESS_MILLIS)
			moveBy(Offset(x = 0f, y = STEP_REORDER_DRAG_DISTANCE))
			up()
		}
		waitForIdle()

		onNodeWithTag("createRecipeStepField0").assertTextContains("Second")
		onNodeWithTag("createRecipeStepField1").assertTextContains("First")
	}

}

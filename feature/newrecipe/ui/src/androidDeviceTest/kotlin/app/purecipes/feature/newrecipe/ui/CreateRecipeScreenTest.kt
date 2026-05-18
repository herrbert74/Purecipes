package app.purecipes.feature.newrecipe.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
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
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import io.kotest.matchers.shouldBe
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
		onNodeWithTag("createRecipeImagePickButton").performScrollTo().assertIsDisplayed()
		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("Trim the carrots")
		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		onNodeWithTag("createRecipeStepField1").performScrollTo().performTextInput("Roast until tender")
		onNodeWithTag("createRecipeSaveButton").performScrollTo().performClick()
		waitForIdle()
		waitUntil(timeoutMillis = 5_000) { repository.savedRequests.size == 1 }

		onNodeWithText("Recipe uploaded.").performScrollTo().assertIsDisplayed()
		repository.savedRequests.size shouldBe 1
		onNodeWithTag("createRecipeSaveButton").assertTextContains("Update recipe")
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

		onNodeWithTag("createRecipeImagePickButton").performScrollTo().performClick()

		onNodeWithText("Importing image").assertIsDisplayed()
		onNodeWithText("Preparing image preview...").performScrollTo().assertIsDisplayed()
		onNodeWithTag("createRecipeImagePickButton").assertIsNotEnabled()
		onNodeWithTag("createRecipeSaveButton").performScrollTo().assertIsNotEnabled()
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

		onNodeWithTag("createRecipeImagePickButton").performScrollTo().performClick()

		onNodeWithText("Could not import the selected image.").performScrollTo().assertIsDisplayed()
		onNodeWithTag("createRecipeImagePickButton").assertIsEnabled()
		onNodeWithTag("createRecipeSaveButton").performScrollTo().assertIsEnabled()
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

		onNodeWithTag("createRecipeCuisineField").performScrollTo().performClick()

		onNodeWithText("No cuisine", useUnmergedTree = true).assertIsDisplayed()
		onNodeWithText("American", useUnmergedTree = true).assertIsDisplayed()
		onAllNodesWithText("Italian", useUnmergedTree = true).fetchSemanticsNodes().size shouldBe 1
		onNodeWithText("American", useUnmergedTree = true).performClick()
		onNodeWithText("American").assertIsDisplayed()
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

		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
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

		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("First")
		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		onNodeWithTag("createRecipeStepField1").performScrollTo().performTextInput("Second")
		onNodeWithTag("createRecipeReorderStepButton1").performScrollTo().performTouchInput {
			down(center)
			advanceEventTime(STEP_REORDER_LONG_PRESS_MILLIS)
			moveBy(Offset(x = 0f, y = STEP_REORDER_DRAG_DISTANCE))
			up()
		}
		waitForIdle()

		onNodeWithTag("createRecipeStepField0").performScrollTo().assertTextContains("Second")
		onNodeWithTag("createRecipeStepField1").performScrollTo().assertTextContains("First")
	}

}

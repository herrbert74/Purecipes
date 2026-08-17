package app.purecipes.feature.newrecipe.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeNutritionEstimateRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

private const val STEP_REORDER_LONG_PRESS_MILLIS = 700L
private const val STEP_REORDER_DRAG_STEPS = 12
private const val STEP_REORDER_DRAG_STEP_DISTANCE = -50f
private const val STEP_REORDER_DRAG_FRAME_DELAY_MILLIS = 16L

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class CreateRecipeScreenTest {

	@Test
	fun createRecipeScreenSavesAndDisplaysRecipe() = runRecompositionTrackingUiTest {
		val repository = FakeCreatedRecipeRepository()
		var saved = false
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					onSaveSuccess = { saved = true },
					viewModel = createRecipeViewModelForTest(repository = repository),
				)
			}
		}

		onNodeWithTag("createRecipeTitleField").performTextInput("Roasted Carrots")
		onNodeWithTag("createRecipeDescriptionField").performTextInput("Sweet and savory side dish.")
		onNodeWithTag("createRecipeImagePickButton").performScrollTo().assertIsDisplayed()
		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("Trim the carrots")
		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		onNodeWithTag("createRecipeStepField1").performScrollTo().performTextInput("Roast until tender")
		onNodeWithTag("createRecipeSaveButton").performClick()
		waitForIdle()
		waitUntil(timeoutMillis = 5_000) { repository.savedRequests.size == 1 }

		onNodeWithText("Recipe uploaded.").performScrollTo().assertIsDisplayed()
		repository.savedRequests.size shouldBe 1
		saved shouldBe true
	}

	@Test
	fun createRecipeScreenDisablesFormWhileImageImportIsInProgress() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
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
		onNodeWithText("Preparing image preview...").assertIsDisplayed()
		onNodeWithTag("createRecipeSaveButton").assertIsNotEnabled()
	}

	@Test
	fun createRecipeScreenShowsImageImportErrorAndReEnablesForm() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
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
		onNodeWithTag("createRecipeSaveButton").assertIsEnabled()
	}

	@Test
	fun createRecipeScreenOpensPaywallWhenFreeUserTapsPrivateRecipe() = runRecompositionTrackingUiTest {
		var openedPaywall: String? = null
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					onOpenPaywall = { feature -> openedPaywall = feature },
					viewModel = createRecipeViewModelForTest(),
				)
			}
		}

		onNodeWithTag("createRecipePrivacySwitch").performScrollTo().assertIsDisplayed()
		onNodeWithText("Private recipe").performScrollTo().performClick()
		waitForIdle()

		openedPaywall shouldBe "private_recipes"
	}

	@Test
	fun createRecipeScreenOpensCuisinePicker() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
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
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
				)
			}
		}

		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		waitForIdle()

		onNodeWithTag("createRecipeStepField1")
			.performScrollTo()
			.assertIsDisplayed()
			.assertIsFocused()
	}

	@Test
	fun createRecipeScreenReordersSteps() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
				)
			}
		}

		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("First")
		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		onNodeWithTag("createRecipeStepField1").performScrollTo().performTextInput("Second")
		waitForIdle()

		onNodeWithTag("createRecipeReorderStepButton1").performScrollTo().performTouchInput {
			down(center)
			advanceEventTime(STEP_REORDER_LONG_PRESS_MILLIS)
			repeat(STEP_REORDER_DRAG_STEPS) {
				moveBy(Offset(x = 0f, y = STEP_REORDER_DRAG_STEP_DISTANCE))
				advanceEventTime(STEP_REORDER_DRAG_FRAME_DELAY_MILLIS)
			}
			up()
		}
		waitForIdle()

		onNodeWithTag("createRecipeStepField0").performScrollTo().assertTextContains("Second")
		onNodeWithTag("createRecipeStepField1").performScrollTo().assertTextContains("First")
	}

	@Test
	fun createRecipeScreenRetainsFormDataAfterConfigurationChange() = runRecompositionTrackingUiTest {
		var compositionGeneration by mutableIntStateOf(0)
		val viewModel = createRecipeViewModelForTest()
		setTrackedContent {
			PurecipesTheme {
				key(compositionGeneration) {
					CreateRecipeScreen(
						canUploadRecipes = true,
						viewModel = viewModel,
					)
				}
			}
		}

		onNodeWithTag("createRecipeTitleField").performTextInput("Roasted Carrots")
		onNodeWithTag("createRecipeDescriptionField").performTextInput("Sweet and savory side dish.")
		onNodeWithTag("createRecipeTotalTimeField").performScrollTo().performTextInput("25")
		onNodeWithTag("createRecipeYieldsField").performScrollTo().performTextInput("4 servings")
		onNodeWithTag("createRecipeIngredientNameField0").performScrollTo().performTextInput("carrots")
		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("Trim the carrots")
		waitForIdle()

		compositionGeneration += 1
		waitForIdle()

		onNodeWithTag("createRecipeTitleField").assertTextContains("Roasted Carrots")
		onNodeWithTag("createRecipeDescriptionField").assertTextContains("Sweet and savory side dish.")
		onNodeWithTag("createRecipeTotalTimeField").performScrollTo().assertTextContains("25")
		onNodeWithTag("createRecipeYieldsField").performScrollTo().assertTextContains("4 servings")
		onNodeWithTag("createRecipeIngredientNameField0").performScrollTo().assertTextContains("carrots")
		onNodeWithTag("createRecipeStepField0").performScrollTo().assertTextContains("Trim the carrots")
	}

	@Test
	fun createRecipeScreenMovesStepUpWithButton() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				CreateRecipeScreen(
					canUploadRecipes = true,
					viewModel = createRecipeViewModelForTest(),
				)
			}
		}

		onNodeWithTag("createRecipeStepField0").performScrollTo().performTextInput("First")
		onNodeWithTag("createRecipeAddStepButton").performScrollTo().performClick()
		onNodeWithTag("createRecipeStepField1").performScrollTo().performTextInput("Second")
		waitForIdle()

		onNodeWithTag("createRecipeMoveStepUpButton1").performScrollTo().performClick()
		waitForIdle()

		onNodeWithTag("createRecipeStepField0").performScrollTo().assertTextContains("Second")
		onNodeWithTag("createRecipeStepField1").performScrollTo().assertTextContains("First")
	}
}

private fun createRecipeViewModelForTest(
	repository: FakeCreatedRecipeRepository = FakeCreatedRecipeRepository(),
	estimateRepository: FakeRecipeNutritionEstimateRepository = FakeRecipeNutritionEstimateRepository(),
): CreateRecipeViewModel = CreateRecipeViewModel(
	getCreatedRecipes = GetCreatedRecipesUseCase(repository),
	saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
	estimateRecipeNutrition = EstimateRecipeNutritionUseCase(estimateRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
	sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
	observePremiumStatus = ObservePremiumStatusUseCase(
		FakeSubscriptionRepository(),
		FakeMonetisationDebugOverridesRepository(),
	),
)

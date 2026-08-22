package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.recipedetails.ui.navigation.installRecipeDetailsFlow
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.SearchListDetailPlaceholder
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.ui.component.NavigationBackHandler
import app.purecipes.shared.ui.theme.PurecipesTheme
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenRecipeSelectionTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Test
	fun selectingTwoDifferentRecipesShowsCorrectDetailsForEach() {
		val environment = recipeSelectionTestEnvironment()
		setUpHarness(environment)

		openRecipeFromSearch(RECIPE_SELECTION_TEST_FIRST_RECIPE_TITLE)
		composeRule.onNodeWithText(RECIPE_SELECTION_TEST_FIRST_RECIPE_DESCRIPTION).assertIsDisplayed()
		composeRule.onAllNodesWithText(RECIPE_SELECTION_TEST_SECOND_RECIPE_DESCRIPTION)
			.assertCountEquals(0)

		pressHardwareBack()
		composeRule.onNodeWithText("2 recipes found").assertIsDisplayed()

		openRecipeFromSearch(RECIPE_SELECTION_TEST_SECOND_RECIPE_TITLE)
		composeRule.onNodeWithText(RECIPE_SELECTION_TEST_SECOND_RECIPE_DESCRIPTION).assertIsDisplayed()
		composeRule.onAllNodesWithText(RECIPE_SELECTION_TEST_FIRST_RECIPE_DESCRIPTION)
			.assertCountEquals(0)
	}

	private fun setUpHarness(environment: RecipeSelectionTestEnvironment) {
		val mainViewModel = environment.mainViewModel
		val searchViewModel = environment.searchViewModel
		composeRule.setContent {
			CompositionLocalProvider(
				LocalMetroViewModelFactory provides environment.metroViewModelFactory,
			) {
				PurecipesTheme {
					LaunchedEffect(mainViewModel) {
						mainViewModel.start()
					}
					val tabBackStack = mainViewModel.rememberActiveTabBackStack()
					val listDetailSceneStrategy = rememberMainListDetailSceneStrategy()
					NavigationBackHandler(
						enabled = true,
						backStackDepth = tabBackStack.size,
						onBack = { mainViewModel.onBack() },
					)
					key(mainViewModel.selectedTab.stackId) {
						NavDisplay(
							backStack = tabBackStack,
							modifier = Modifier.fillMaxSize(),
							sceneStrategies = listOf(listDetailSceneStrategy),
							entryProvider = entryProvider {
								entry<SearchDestination>(
									metadata = ListDetailSceneStrategy.listPane(
										detailPlaceholder = { SearchListDetailPlaceholder() },
									),
								) {
									RecipeSearchScreen(
										modifier = Modifier.fillMaxSize(),
										isSignedIn = true,
										onRecipeSelect = mainViewModel::onRecipeSelected,
										onRequestLogInForFilters = {},
										viewModel = searchViewModel,
									)
								}
								installRecipeDetailsFlow(
									navigator = mainViewModel.navigator,
									canManageFavorites = true,
									sessionKey = null,
									onStartCooking = mainViewModel::onStartCooking,
									onOpenMeasurementPreferences = {},
								)
							},
						)
					}
				}
			}
		}
		composeRule.waitForIdle()
		composeRule.onNodeWithText("2 recipes found").assertIsDisplayed()
	}

	private fun openRecipeFromSearch(title: String) {
		composeRule.onNodeWithText(title).performClick()
		composeRule.waitForIdle()
	}

	private fun pressHardwareBack() {
		composeRule.runOnIdle {
			composeRule.activity.onBackPressedDispatcher.onBackPressed()
		}
		composeRule.waitForIdle()
	}
}

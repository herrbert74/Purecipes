package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.ui.component.NavigationBackHandler
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenHardwareBackTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Test
	fun hardwareBackFromRecipeDetailsReturnsToSearch() {
		val mainViewModel = mainViewModelForDeviceTest()
		val recipeId = HARDWARE_BACK_TEST_RECIPE_ID
		val searchViewModel = recipeSearchViewModelForDeviceTest(
			searchRepository = FakeRecipeSearchRepository(
				result = Ok(
					listOf(
						RecipeSummary(
							id = recipeId,
							title = "Roasted Carrots",
							cuisine = Cuisine.MEDITERRANEAN,
							imageUrl = null,
							totalTime = 35,
						),
					),
				),
			),
		)
		val recipeDetailsViewModel = recipeDetailsViewModelForDeviceTest(
			recipeId = recipeId,
			recipeDetailsRepository = FakeRecipeDetailsRepository(
				fakeRecipeDetails(
					id = recipeId,
					title = "Roasted Carrots",
					description = "Sweet and savory side dish.",
				),
			),
		)

		composeRule.setContent {
			PurecipesTheme {
				LaunchedEffect(mainViewModel) {
					mainViewModel.start()
				}
				val backStack = mainViewModel.mainBackStack()
				NavigationBackHandler(
					enabled = true,
					backStackDepth = backStack.size,
					onBack = { mainViewModel.onBack() },
				)
				NavDisplay(
					backStack = backStack,
					modifier = Modifier.fillMaxSize(),
					onBack = { mainViewModel.onBack() },
					entryProvider = entryProvider {
						entry<SearchDestination> {
							RecipeSearchScreen(
								modifier = Modifier.fillMaxSize(),
								isSignedIn = true,
								onRecipeSelect = mainViewModel::onRecipeSelected,
								onRequestLogInForFilters = {},
								viewModel = searchViewModel,
							)
						}
						entry<RecipeDetailsDestination> { destination ->
							RecipeDetailsScreen(
								recipeId = destination.recipeId,
								canManageFavorites = true,
								onOpenMeasurementPreferences = {},
								onBack = { mainViewModel.onBack() },
								onStartCooking = {},
								viewModel = recipeDetailsViewModel,
							)
						}
					},
				)
			}
		}

		composeRule.runOnIdle {
			mainViewModel.onRecipeSelected(recipeId)
		}

		composeRule.waitForIdle()
		composeRule.onNodeWithText("Sweet and savory side dish.").assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()

		composeRule.runOnIdle {
			composeRule.activity.onBackPressedDispatcher.onBackPressed()
		}

		composeRule.waitForIdle()
		composeRule.onNodeWithText("1 recipes found").assertIsDisplayed()
		composeRule.onAllNodesWithText("Start cooking").assertCountEquals(0)
	}
}

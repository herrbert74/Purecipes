package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.favorites.ui.FavoritesScreen
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.ui.component.NavigationBackHandler
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenHardwareBackTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Test
	fun hardwareBackFromRecipeDetailsReturnsToSearch() {
		val environment = hardwareBackTestEnvironment()
		setUpHarness(environment)

		openRecipeDetails(environment)
		pressHardwareBack()

		composeRule.onNodeWithText("1 recipes found").assertIsDisplayed()
		composeRule.onAllNodesWithText("Start cooking").assertCountEquals(0)
	}

	@Test
	fun switchingTabsPreservesSearchTabDepth() {
		val environment = hardwareBackTestEnvironment()
		setUpHarness(environment)

		openRecipeDetails(environment)
		selectMainTab(environment, MainTabStackId.Favorites)
		composeRule.onNodeWithText("No favorites yet").assertIsDisplayed()
		selectMainTab(environment, MainTabStackId.Search)

		composeRule.onNodeWithText(HARDWARE_BACK_TEST_RECIPE_DESCRIPTION).assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()
	}

	@Test
	fun reTappingActiveSearchTabPopsToRoot() {
		val environment = hardwareBackTestEnvironment()
		setUpHarness(environment)

		openRecipeDetails(environment)
		selectMainTab(environment, MainTabStackId.Search)

		composeRule.onNodeWithText("1 recipes found").assertIsDisplayed()
		composeRule.onAllNodesWithText("Start cooking").assertCountEquals(0)
	}

	@Test
	fun hardwareBackFromFavoritesRootReturnsToSearch() {
		val environment = hardwareBackTestEnvironment()
		setUpHarness(environment)

		selectMainTab(environment, MainTabStackId.Favorites)
		composeRule.onNodeWithText("No favorites yet").assertIsDisplayed()
		pressHardwareBack()

		composeRule.onNodeWithText("1 recipes found").assertIsDisplayed()
	}

	private fun setUpHarness(environment: HardwareBackTestEnvironment) {
		val mainViewModel = environment.mainViewModel
		val searchViewModel = environment.searchViewModel
		val recipeDetailsViewModel = environment.recipeDetailsViewModel
		val favoritesViewModel = environment.favoritesViewModel
		composeRule.setContent {
			PurecipesTheme {
				LaunchedEffect(mainViewModel) {
					mainViewModel.start()
				}
				val tabBackStack = mainViewModel.rememberActiveTabBackStack()
				NavigationBackHandler(
					enabled = true,
					backStackDepth = tabBackStack.size,
					onBack = {
						if (!mainViewModel.onBack() && mainViewModel.shouldExit()) {
							Unit
						}
					},
				)
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					bottomBar = {
						NavigationBar {
							mainTabs.forEach { tab ->
								NavigationBarItem(
									selected = tab.stackId == mainViewModel.selectedTab.stackId,
									onClick = { mainViewModel.onTabSelected(tab) },
									icon = {
										Icon(
											imageVector = tab.icon,
											contentDescription = tab.label,
										)
									},
									label = { Text(text = tab.label) },
								)
							}
						}
					},
				) { innerPadding ->
					key(mainViewModel.selectedTab.stackId) {
						NavDisplay(
							backStack = tabBackStack,
							modifier = Modifier
								.fillMaxSize()
								.padding(innerPadding),
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
								entry<FavoritesDestination> {
									FavoritesScreen(
										modifier = Modifier.fillMaxSize(),
										sessionKey = "hardware-back-test",
										onRecipeSelect = mainViewModel::onRecipeSelected,
										viewModel = favoritesViewModel,
									)
								}
							},
						)
					}
				}
			}
		}
		composeRule.waitForIdle()
		composeRule.onNodeWithText("1 recipes found").assertIsDisplayed()
	}

	private fun openRecipeDetails(environment: HardwareBackTestEnvironment) {
		composeRule.runOnIdle {
			environment.mainViewModel.onRecipeSelected(environment.recipeId)
		}
		composeRule.waitForIdle()
		composeRule.onNodeWithText(HARDWARE_BACK_TEST_RECIPE_DESCRIPTION).assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()
	}

	private fun selectMainTab(environment: HardwareBackTestEnvironment, stackId: MainTabStackId) {
		composeRule.runOnIdle {
			environment.mainViewModel.onTabSelected(mainTabs.first { it.stackId == stackId })
		}
		composeRule.waitForIdle()
	}

	private fun pressHardwareBack() {
		composeRule.runOnIdle {
			composeRule.activity.onBackPressedDispatcher.onBackPressed()
		}
		composeRule.waitForIdle()
	}
}

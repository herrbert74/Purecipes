package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.model.AnalyticsScreenName
import app.purecipes.feature.library.ui.LibraryListDetailPlaceholder
import app.purecipes.feature.library.ui.LibraryScreen
import app.purecipes.feature.library.ui.navigation.LibraryDestination
import app.purecipes.feature.main.ui.analytics.TrackActiveScreenViews
import app.purecipes.feature.recipedetails.ui.RECIPE_DETAILS_CONTENT_TAG
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.SearchListDetailPlaceholder
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.ui.component.NavigationBackHandler
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenScreenViewTrackingTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Test
	fun navigatingAndReturningEmitsScreenViewsIncludingResurface() {
		val analyticsRepository = FakeAnalyticsRepository()
		val environment = hardwareBackTestEnvironment(analyticsRepository = analyticsRepository)
		setUpHarness(environment)

		composeRule.runOnIdle {
			assertEquals(
				listOf(AnalyticsScreenName.SEARCH),
				analyticsRepository.trackedScreenViews.map { it.screenName },
			)
		}

		composeRule.runOnIdle {
			environment.mainViewModel.onRecipeSelected(environment.recipeId)
		}
		composeRule.waitForIdle()
		assertRecipeDetailsScreenDisplayed()

		composeRule.runOnIdle {
			assertEquals(
				listOf(
					AnalyticsScreenName.SEARCH,
					AnalyticsScreenName.RECIPE_DETAILS,
				),
				analyticsRepository.trackedScreenViews.map { it.screenName },
			)
		}

		composeRule.runOnIdle {
			environment.mainViewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Library })
		}
		composeRule.waitForIdle()
		composeRule.onNodeWithText("No favorites yet").assertIsDisplayed()

		composeRule.runOnIdle {
			assertEquals(
				listOf(
					AnalyticsScreenName.SEARCH,
					AnalyticsScreenName.RECIPE_DETAILS,
					AnalyticsScreenName.FAVORITES,
				),
				analyticsRepository.trackedScreenViews.map { it.screenName },
			)
		}

		composeRule.runOnIdle {
			environment.mainViewModel.onTabSelected(mainTabs.first { it.stackId == MainTabStackId.Search })
		}
		composeRule.waitForIdle()
		assertRecipeDetailsScreenDisplayed()

		composeRule.runOnIdle {
			assertEquals(
				listOf(
					AnalyticsScreenName.SEARCH,
					AnalyticsScreenName.RECIPE_DETAILS,
					AnalyticsScreenName.FAVORITES,
					AnalyticsScreenName.RECIPE_DETAILS,
				),
				analyticsRepository.trackedScreenViews.map { it.screenName },
			)
		}

		composeRule.runOnIdle {
			composeRule.activity.onBackPressedDispatcher.onBackPressed()
		}
		composeRule.waitForIdle()
		composeRule.onNodeWithText("1 recipes found")
			.performScrollTo()
			.assertIsDisplayed()

		composeRule.runOnIdle {
			assertEquals(
				listOf(
					AnalyticsScreenName.SEARCH,
					AnalyticsScreenName.RECIPE_DETAILS,
					AnalyticsScreenName.FAVORITES,
					AnalyticsScreenName.RECIPE_DETAILS,
					AnalyticsScreenName.SEARCH,
				),
				analyticsRepository.trackedScreenViews.map { it.screenName },
			)
		}
	}

	private fun setUpHarness(environment: HardwareBackTestEnvironment) {
		val mainViewModel = environment.mainViewModel
		val searchViewModel = environment.searchViewModel
		val recipeDetailsViewModel = environment.recipeDetailsViewModel
		val favoritesViewModel = environment.libraryViewModel
		composeRule.setContent {
			PurecipesTheme {
				LaunchedEffect(mainViewModel) {
					mainViewModel.start()
				}
				val tabBackStack = mainViewModel.rememberActiveTabBackStack()
				val listDetailSceneStrategy = rememberMainListDetailSceneStrategy()
				TrackActiveScreenViews(
					selectedTab = mainViewModel.selectedTab,
					backStack = tabBackStack,
					screenViewTracker = mainViewModel.screenViewTracker,
				)
				NavigationBackHandler(
					enabled = true,
					backStackDepth = tabBackStack.size,
					onBack = {
						if (!mainViewModel.onBack() && mainViewModel.shouldExit()) {
							@Suppress("UnusedExpression")
							Unit
						}
					},
				)
				MainNavigationSuiteScaffold(
					selectedTab = mainViewModel.selectedTab,
					onTabSelect = mainViewModel::onTabSelected,
				) {
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
								entry<RecipeDetailsDestination>(
									metadata = ListDetailSceneStrategy.detailPane(),
								) { destination ->
									RecipeDetailsScreen(
										recipeId = destination.recipeId,
										canManageFavorites = true,
										onOpenMeasurementPreferences = {},
										onBack = { mainViewModel.onBack() },
										onStartCooking = {},
										viewModel = recipeDetailsViewModel,
									)
								}
								entry<LibraryDestination>(
									metadata = ListDetailSceneStrategy.listPane(
										detailPlaceholder = { LibraryListDetailPlaceholder() },
									),
								) {
									LibraryScreen(
										modifier = Modifier.fillMaxSize(),
										sessionKey = "screen-view-test",
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
		composeRule.onNodeWithText("1 recipes found")
			.performScrollTo()
			.assertIsDisplayed()
	}

	private fun assertRecipeDetailsScreenDisplayed() {
		composeRule.onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText(HARDWARE_BACK_TEST_RECIPE_DESCRIPTION))
		composeRule.onNodeWithText(HARDWARE_BACK_TEST_RECIPE_DESCRIPTION).assertIsDisplayed()
	}
}

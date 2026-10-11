package app.purecipes.feature.main.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.home.ui.HomeListDetailPlaceholder
import app.purecipes.feature.home.ui.HomeScreen
import app.purecipes.feature.home.ui.navigation.HomeDestination
import app.purecipes.feature.recipedetails.ui.RECIPE_DETAILS_CONTENT_TAG
import app.purecipes.feature.recipedetails.ui.RecipeDetailsScreen
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.RecipeSearchScreen
import app.purecipes.feature.search.ui.SearchListDetailPlaceholder
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.ui.component.NavigationBackHandler
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class MainScreenHomeRestorationTest {

	@get:Rule
	val composeRule = createAndroidComposeRule<ComponentActivity>()

	@Test
	fun removingAFavoriteOnRecipeDetailsUpdatesTheHomeShelf() {
		val kept = recipeSummary(id = 1, title = KEPT_RECIPE_TITLE)
		val removed = recipeSummary(id = REMOVED_RECIPE_ID, title = REMOVED_RECIPE_TITLE)
		val favorites = FakeFavoritesRepository()
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(
				HomeFeed(
					shelves = listOf(
						HomeShelf(
							id = HomeShelfId.FAVORITES,
							title = "Your favorites",
							recipes = listOf(kept, removed),
						),
					),
				),
			),
		)
		setUpHarness(
			homeFeedRepository = homeFeedRepository,
			favoritesRepository = favorites,
			sessionKey = SESSION_KEY,
			removedRecipeTitle = REMOVED_RECIPE_TITLE,
		)

		composeRule.onNodeWithText(REMOVED_RECIPE_TITLE).performClick()
		composeRule.waitForIdle()
		homeFeedRepository.result = Ok(
			HomeFeed(
				shelves = listOf(
					HomeShelf(
						id = HomeShelfId.FAVORITES,
						title = "Your favorites",
						recipes = listOf(kept),
					),
				),
			),
		)
		composeRule.onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Remove from favorites"))
		composeRule.onNodeWithText("Remove from favorites").performClick()
		composeRule.waitForIdle()
		pressHardwareBack()

		composeRule.onNodeWithText(KEPT_RECIPE_TITLE).assertIsDisplayed()
		composeRule.onNodeWithText(REMOVED_RECIPE_TITLE).assertDoesNotExist()
	}

	private fun setUpHarness(
		homeFeedRepository: FakeHomeFeedRepository,
		favoritesRepository: FakeFavoritesRepository,
		sessionKey: String?,
		removedRecipeTitle: String,
	) {
		val mainViewModel = mainViewModelForDeviceTest()
		val homeViewModel = homeViewModelForDeviceTest(
			homeFeedRepository = homeFeedRepository,
			favoritesRepository = favoritesRepository,
			sessionKey = sessionKey,
		)
		val searchViewModel = recipeSearchViewModelForDeviceTest()
		val recipeDetailsViewModel = recipeDetailsViewModelForDeviceTest(
			recipeId = REMOVED_RECIPE_ID,
			recipeDetailsRepository = FakeRecipeDetailsRepository(
				fakeRecipeDetails(
					id = REMOVED_RECIPE_ID,
					title = removedRecipeTitle,
					description = "A saved recipe.",
				).copy(isFavorite = true),
			),
			favoritesRepository = favoritesRepository,
			sessionKey = sessionKey,
		)
		composeRule.setContent {
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
							entry<HomeDestination>(
								metadata = ListDetailSceneStrategy.listPane(
									detailPlaceholder = { HomeListDetailPlaceholder() },
								),
							) {
								HomeScreen(
									modifier = Modifier.fillMaxSize(),
									onRecipeSelect = mainViewModel::onRecipeSelected,
									onOpenSearch = { filters, seeAllShelf ->
										mainViewModel.navigator.replaceTabRoot(
											SearchDestination(
												initialFilters = filters,
												seeAllShelf = seeAllShelf,
											),
										)
									},
									sessionKey = sessionKey,
									viewModel = homeViewModel,
								)
							}
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
									sessionKey = sessionKey,
									viewModel = recipeDetailsViewModel,
								)
							}
						},
					)
				}
			}
		}
		composeRule.waitForIdle()
	}

	private fun pressHardwareBack() {
		composeRule.runOnIdle {
			composeRule.activity.onBackPressedDispatcher.onBackPressed()
		}
		composeRule.waitForIdle()
	}

	private fun recipeSummary(
		id: Int,
		title: String,
	) = RecipeSummary(
		id = id,
		title = title,
		cuisine = null,
		imageUrl = null,
		totalTime = 20,
		isFavorite = true,
	)

	private companion object {

		const val SESSION_KEY = "home-restoration"
		const val KEPT_RECIPE_TITLE = "Kept pasta"
		const val REMOVED_RECIPE_TITLE = "Removed pasta"
		const val REMOVED_RECIPE_ID = 2
	}
}

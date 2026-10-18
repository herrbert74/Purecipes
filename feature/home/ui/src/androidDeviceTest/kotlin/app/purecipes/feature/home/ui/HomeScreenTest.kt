package app.purecipes.feature.home.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.home.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.home.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.recipedetails.domain.usecase.ObserveRecipeViewsUseCase
import app.purecipes.shared.data.readiness.SearchReadinessCoordinator
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeActivityRepository
import app.purecipes.shared.testfixtures.uitest.UiTestEssential
import app.purecipes.shared.testfixtures.uitest.UiTestHome
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.collections.immutable.persistentMapOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
@UiTestHome
class HomeScreenTest {

	@Test
	@UiTestEssential
	fun loadingHomeShowsShelfSkeleton() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				HomeFeedContent(
					isLoading = true,
					errorMessage = null,
					homeFeed = HomeFeed(),
					shelfPagination = persistentMapOf(),
					onRecipeSelect = { _, _ -> },
					onSeeAll = {},
					onFeatureRequestClick = {},
					onRetry = {},
				)
			}
		}

		onNodeWithTag(HOME_FEED_SKELETON_TAG).assertIsDisplayed()
		onNodeWithTag(HOME_TITLE_TAG).assertIsDisplayed()
		onNodeWithText("Purecipes").assertIsDisplayed()
	}

	@Test
	fun removingAFavoriteDropsItFromTheFavoritesShelf() = runRecompositionTrackingUiTest {
		val kept = recipeSummary(id = 1, title = KEPT_RECIPE_TITLE)
		val removed = recipeSummary(id = 2, title = REMOVED_RECIPE_TITLE)
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
		val viewModel = homeViewModel(
			homeFeedRepository = homeFeedRepository,
			favoritesRepository = favorites,
			sessionKey = SESSION_KEY,
		)
		setTrackedContent {
			PurecipesTheme {
				HomeScreen(
					sessionKey = SESSION_KEY,
					viewModel = viewModel,
				)
			}
		}
		waitUntil(timeoutMillis = HOME_CONTENT_TIMEOUT_MILLIS) {
			onAllNodesWithText(KEPT_RECIPE_TITLE).fetchSemanticsNodes().isNotEmpty()
		}

		onNodeWithTag(HOME_TITLE_TAG).assertIsDisplayed()
		onNodeWithText(KEPT_RECIPE_TITLE).assertIsDisplayed()
		onNodeWithText(REMOVED_RECIPE_TITLE).assertIsDisplayed()
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
		favorites.emitFavoriteEvent(FavoriteEvent.Removed(removed.id))
		waitForIdle()

		onNodeWithText(KEPT_RECIPE_TITLE).assertIsDisplayed()
		onNodeWithText(REMOVED_RECIPE_TITLE).assertDoesNotExist()
	}

	@Test
	fun returningToHomeKeepsTheScrolledShelfVisible() = runRecompositionTrackingUiTest {
		val viewModel = homeViewModel(
			homeFeedRepository = FakeHomeFeedRepository(
				result = Ok(HomeFeed(shelves = scrollableShelves())),
			),
		)
		var showHome by mutableStateOf(true)
		setTrackedContent {
			PurecipesTheme {
				if (showHome) {
					HomeScreen(
						onOpenShelf = {
							showHome = false
						},
						viewModel = viewModel,
						modifier = Modifier
							.fillMaxWidth()
							.height(HOME_LIST_VIEWPORT_HEIGHT),
					)
				} else {
					Text(text = "Favorites list")
				}
			}
		}
		waitUntil(timeoutMillis = HOME_CONTENT_TIMEOUT_MILLIS) {
			onAllNodesWithText(FIRST_SHELF_TITLE).fetchSemanticsNodes().isNotEmpty()
		}

		onNodeWithText(FIRST_SHELF_TITLE).assertIsDisplayed()
		onAllNodesWithText(LAST_SHELF_TITLE).assertCountEquals(0)
		onNodeWithTag(HOME_FEED_LIST_TAG).performScrollToNode(hasText(LAST_SHELF_TITLE))
		onNodeWithText(LAST_SHELF_TITLE).assertIsDisplayed()
		onNodeWithTag("$HOME_SHELF_SEE_ALL_TAG_PREFIX${HomeShelfId.HABIT.name}").performClick()
		onNodeWithText("Favorites list").assertIsDisplayed()
		runOnIdle {
			showHome = true
		}

		onNodeWithText(LAST_SHELF_TITLE).assertIsDisplayed()
		onAllNodesWithText(FIRST_SHELF_TITLE).assertCountEquals(0)
	}

	private fun homeViewModel(
		homeFeedRepository: FakeHomeFeedRepository,
		favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
		sessionKey: String? = null,
	) = HomeViewModel(
		getHomeFeed = GetHomeFeedUseCase(homeFeedRepository),
		getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
		homeFeedRefresh = HomeFeedRefreshCoordinator(),
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
		observeRecipeViews = ObserveRecipeViewsUseCase(FakeRecipeActivityRepository()),
		searchReadiness = SearchReadinessCoordinator(),
		sessionKey = sessionKey,
	)

	private fun scrollableShelves(): List<HomeShelf> = listOf(
		shelf(id = HomeShelfId.NEW, title = FIRST_SHELF_TITLE, recipeId = 11),
		shelf(id = HomeShelfId.RIGHT_NOW, title = "Right now shelf", recipeId = 12),
		shelf(id = HomeShelfId.QUICK, title = "Quick shelf", recipeId = 13),
		shelf(id = HomeShelfId.FAVORITES, title = "Favorites shelf", recipeId = 14),
		shelf(id = HomeShelfId.HABIT, title = LAST_SHELF_TITLE, recipeId = 15),
	)

	private fun shelf(
		id: HomeShelfId,
		title: String,
		recipeId: Int,
	) = HomeShelf(
		id = id,
		title = title,
		recipes = listOf(recipeSummary(id = recipeId, title = "Card $recipeId")),
	)

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

		const val SESSION_KEY = "home-screen-test"
		const val KEPT_RECIPE_TITLE = "Kept pasta"
		const val REMOVED_RECIPE_TITLE = "Removed pasta"
		const val FIRST_SHELF_TITLE = "New shelf"
		const val LAST_SHELF_TITLE = "Habit shelf"
		const val HOME_CONTENT_TIMEOUT_MILLIS = 5_000L
		val HOME_LIST_VIEWPORT_HEIGHT = 220.dp
	}
}

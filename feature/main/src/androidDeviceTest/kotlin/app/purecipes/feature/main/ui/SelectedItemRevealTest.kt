package app.purecipes.feature.main.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.home.ui.HomeListDetailPlaceholder
import app.purecipes.feature.home.ui.HomeScreen
import app.purecipes.feature.home.ui.navigation.HomeDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.shelfKey
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.uitest.UiTestMain
import app.purecipes.shared.ui.component.paging.rememberLazyGridSelectedItemReveal
import app.purecipes.shared.ui.component.paging.rememberLazyListSelectedItemReveal
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
@UiTestMain
class SelectedItemRevealTest {

	@Test
	fun lazyRowScrollsRightEdgeItemIntoViewWhenContainerShrinks() = runComposeUiTest {
		lateinit var listState: androidx.compose.foundation.lazy.LazyListState
		setContent {
			PurecipesTheme {
				var shrunk by remember { mutableStateOf(false) }
				val width by animateDpAsState(targetValue = if (shrunk) 420.dp else 720.dp)
				listState = rememberLazyListState()
				val revealSelectedItem = rememberLazyListSelectedItemReveal(listState)
				LazyRow(
					state = listState,
					modifier = Modifier.width(width),
					contentPadding = PaddingValues(horizontal = 16.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
				) {
					items(count = 10, key = { it }) { index ->
						Box(
							modifier = Modifier
								.size(width = 180.dp, height = 160.dp)
								.clickable {
									revealSelectedItem(index, index)
									shrunk = true
								},
						) {
							Text(text = "Shelf item $index")
						}
					}
				}
			}
		}

		onNodeWithText("Shelf item 3").performClick()
		waitForIdle()

		val layoutInfo = listState.layoutInfo
		val visibleEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
		val item = layoutInfo.visibleItemsInfo.firstOrNull { it.key == 3 }
		assertNotNull("Expected selected shelf item 3 to remain visible", item)
		val visibleItem = requireNotNull(item)
		assertTrue("Expected item start >= 0 but was ${visibleItem.offset}", visibleItem.offset >= 0)
		assertTrue(
			"Expected item end ${visibleItem.offset + visibleItem.size} <= $visibleEnd",
			visibleItem.offset + visibleItem.size <= visibleEnd,
		)
	}

	@Test
	fun lazyGridScrollsBottomRightItemIntoViewWhenColumnsDrop() = runComposeUiTest {
		lateinit var gridState: androidx.compose.foundation.lazy.grid.LazyGridState
		setContent {
			PurecipesTheme {
				var shrunk by remember { mutableStateOf(false) }
				val width by animateDpAsState(targetValue = if (shrunk) 440.dp else 680.dp)
				gridState = rememberLazyGridState()
				val revealSelectedItem = rememberLazyGridSelectedItemReveal(gridState)
				LazyVerticalGrid(
					columns = GridCells.Adaptive(200.dp),
					state = gridState,
					modifier = Modifier
						.width(width)
						.height(480.dp),
					contentPadding = PaddingValues(16.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
				) {
					items(count = 15, key = { it }) { index ->
						Box(
							modifier = Modifier
								.height(140.dp)
								.clickable {
									revealSelectedItem(index, index)
									shrunk = true
								},
						) {
							Text(text = "Grid item $index")
						}
					}
				}
			}
		}

		onNodeWithText("Grid item 8").performClick()
		waitForIdle()

		val layoutInfo = gridState.layoutInfo
		val visibleEnd = layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
		val item = layoutInfo.visibleItemsInfo.firstOrNull { it.key == 8 }
		assertNotNull("Expected selected grid item 8 to remain visible", item)
		val visibleItem = requireNotNull(item)
		assertTrue("Expected item top >= 0 but was ${visibleItem.offset.y}", visibleItem.offset.y >= 0)
		assertTrue(
			"Expected item bottom ${visibleItem.offset.y + visibleItem.size.height} <= $visibleEnd",
			visibleItem.offset.y + visibleItem.size.height <= visibleEnd,
		)
	}

	@Test
	fun homeScreenShelfScrollsSelectedRightEdgeRecipeIntoViewInListDetail() = runComposeUiTest {
		val recipes = (1..10).map { index ->
			RecipeSummary(
				id = index,
				title = "Home Shelf Recipe $index",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 20,
			)
		}
		val homeViewModel = homeViewModelForDeviceTest(
			homeFeedRepository = FakeHomeFeedRepository(
				result = Ok(
					HomeFeed(
						shelves = listOf(
							HomeShelf(
								id = HomeShelfId.NEW,
								title = "New",
								recipes = recipes,
							),
						),
					),
				),
			),
		)
		setContent {
			PurecipesTheme {
				val backStack = remember { mutableStateListOf<NavKey>(HomeDestination) }
				val listDetailSceneStrategy = rememberMainListDetailSceneStrategy(
					hasDetailPane = showsDetailPane(backStack),
				)
				NavDisplay(
					backStack = backStack,
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
								onRecipeSelect = { recipeId, _ ->
									backStack.add(RecipeDetailsDestination(recipeId = recipeId))
								},
								viewModel = homeViewModel,
							)
						}
						entry<RecipeDetailsDestination>(
							metadata = ListDetailSceneStrategy.detailPane(),
						) { destination ->
							Text(text = "Detail for ${destination.recipeId}")
						}
					},
				)
			}
		}

		waitUntil(timeoutMillis = 5_000L) {
			runCatching {
				onNodeWithText("Home Shelf Recipe 5").assertIsDisplayed()
				true
			}.getOrDefault(false)
		}

		onNodeWithText("Home Shelf Recipe 5").performClick()
		waitForIdle()
		waitUntil(timeoutMillis = 5_000L) {
			runCatching {
				onNodeWithText("Detail for 5").assertIsDisplayed()
				true
			}.getOrDefault(false)
		}
		mainClock.advanceTimeBy(REVEAL_SETTLE_WAIT_MS)
		waitForIdle()

		assertTrue(
			"Expected Home shelf to scroll right when opening detail pane, but firstVisibleItemIndex=" +
				"${homeViewModel.shelfFirstVisibleItemIndex("NEW")}, " +
				"offset=${homeViewModel.shelfFirstVisibleItemScrollOffset("NEW")}, " +
				"shelves=${homeViewModel.homeFeed.shelves.map { it.shelfKey() }}",
			homeViewModel.shelfFirstVisibleItemIndex("NEW") > 0 ||
				homeViewModel.shelfFirstVisibleItemScrollOffset("NEW") > 0,
		)
	}
}

private const val REVEAL_SETTLE_WAIT_MS = 1_200L

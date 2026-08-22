package app.purecipes.feature.library.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.CookbookMembershipEvent
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveRecipeFromCookbookUseCase
import app.purecipes.feature.library.ui.cookbooks.CREATE_COOKBOOK_DIALOG_INPUT_TAG
import app.purecipes.feature.library.ui.cookbooks.DELETE_COOKBOOK_BUTTON_PREFIX
import app.purecipes.feature.library.ui.cookbooks.DELETE_COOKBOOK_DIALOG_CONFIRM_TAG
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.ui.component.RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class LibraryScreenTest {

	@Test
	fun favoritesScreenDoesNotRecomposeTitle() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				LibraryScreen(
					sessionKey = "session",
					viewModel = favoritesViewModelForTest(),
					onRecipeSelect = {},
				)
			}
		}
		onNodeWithText("Favorites").assertIsDisplayed()
		onNodeWithTag(LIBRARY_TITLE_TAG).assertStable()
	}

	@Test
	fun cookbookDeleteButtonDisabledForNonEmptyCookbook() = runRecompositionTrackingUiTest {
		val cookbooksRepo = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(
						CookbookSummary(
							id = NON_EMPTY_COOKBOOK_ID,
							name = "Non-empty cookbook",
							recipeCount = 3,
							updatedAtEpochMillis = 0L,
						),
					),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		setTrackedContent {
			PurecipesTheme {
				LibraryScreen(
					sessionKey = "session",
					viewModel = favoritesViewModelForTest(cookbooksRepository = cookbooksRepo),
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		onAllNodesWithText("Delete")[0].assertIsNotEnabled()
	}

	@Test
	fun createCookbookInvokesCreateUseCase() = runRecompositionTrackingUiTest {
		val cookbooksRepo = FakeCookbooksRepository()
		setTrackedContent {
			PurecipesTheme {
				LibraryScreen(
					sessionKey = "session",
					viewModel = favoritesViewModelForTest(cookbooksRepository = cookbooksRepo),
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		onNodeWithText("Create new cookbook").performClick()
		onNodeWithTag(CREATE_COOKBOOK_DIALOG_INPUT_TAG).performTextInput("Weekend")
		onNodeWithText("Create").performClick()

		waitForIdle()
		assertEquals(1, cookbooksRepo.createCookbookCallCount)
	}

	@Test
	fun deleteCookbookRequiresConfirmationAndInvokesDeleteUseCase() = runRecompositionTrackingUiTest {
		val cookbooksRepo = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(
						CookbookSummary(
							id = EMPTY_COOKBOOK_ID,
							name = "Empty cookbook",
							recipeCount = 0,
							updatedAtEpochMillis = 0L,
						),
					),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		setTrackedContent {
			PurecipesTheme {
				LibraryScreen(
					sessionKey = "session",
					viewModel = favoritesViewModelForTest(cookbooksRepository = cookbooksRepo),
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		val deleteCookbookButtonTag = "$DELETE_COOKBOOK_BUTTON_PREFIX$EMPTY_COOKBOOK_ID"
		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithTag(deleteCookbookButtonTag).fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithTag(deleteCookbookButtonTag).performClick()
		onNodeWithText("Delete cookbook?", useUnmergedTree = true).assertIsDisplayed()
		onNodeWithTag(DELETE_COOKBOOK_DIALOG_CONFIRM_TAG, useUnmergedTree = true).performClick()

		waitUntil(timeoutMillis = 5_000) {
			cookbooksRepo.deleteCookbookCallCount == 1
		}
		assertEquals(1, cookbooksRepo.deleteCookbookCallCount)
	}

	@Test
	fun cookbookDetailRefreshRemovesRecipeAfterFavoriteEvent() = runRecompositionTrackingUiTest {
		val cookbooksRepo = MutableCookbooksRepository()
		val favoritesRepo = FakeFavoritesRepository()
		val viewModel = cookbookDetailViewModelForTest(
			cookbooksRepository = cookbooksRepo,
			favoritesRepository = favoritesRepo,
		)
		setTrackedContent {
			PurecipesTheme {
				CookbookDetailScreen(
					cookbookId = TEST_COOKBOOK_ID,
					name = TEST_COOKBOOK_NAME,
					sessionKey = "session",
					onBack = {},
					onRecipeSelect = {},
					viewModel = viewModel,
				)
			}
		}
		onNodeWithText("1 recipes").assertIsDisplayed()

		runOnIdle {
			cookbooksRepo.setCookbookRecipes(emptyList())
			favoritesRepo.emitFavoriteEvent(FavoriteEvent.Removed(recipeId = TEST_RECIPE_ID))
		}

		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithText(TEST_RECIPE_TITLE).fetchSemanticsNodes().isEmpty()
		}
		onNodeWithText("0 recipes").assertIsDisplayed()
	}

	@Test
	fun cookbookDetailRemoveRecipeInvokesRemoveUseCase() = runRecompositionTrackingUiTest {
		val cookbooksRepo = MutableCookbooksRepository()
		val viewModel = cookbookDetailViewModelForTest(cookbooksRepository = cookbooksRepo)
		setTrackedContent {
			PurecipesTheme {
				CookbookDetailScreen(
					cookbookId = TEST_COOKBOOK_ID,
					name = TEST_COOKBOOK_NAME,
					sessionKey = "session",
					onBack = {},
					onRecipeSelect = {},
					viewModel = viewModel,
				)
			}
		}

		onNodeWithText(TEST_RECIPE_TITLE).assertIsDisplayed()
		val deleteButtonTag = "$RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX$TEST_RECIPE_ID"
		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithTag(deleteButtonTag).fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithTag(deleteButtonTag).performClick()

		waitUntil(timeoutMillis = 5_000) {
			cookbooksRepo.removeRecipeFromCookbookCallCount == 1 &&
				onAllNodesWithText(TEST_RECIPE_TITLE).fetchSemanticsNodes().isEmpty()
		}
		assertEquals(1, cookbooksRepo.removeRecipeFromCookbookCallCount)
		onNodeWithText("0 recipes").assertIsDisplayed()
	}

	private companion object {

		const val NON_EMPTY_COOKBOOK_ID = 21
		const val EMPTY_COOKBOOK_ID = 22
		const val TEST_COOKBOOK_ID = 23
		const val TEST_COOKBOOK_NAME = "Weeknight dinners"
		const val TEST_RECIPE_ID = 77
		const val TEST_RECIPE_TITLE = "Creamy tomato pasta"
	}

	private class MutableCookbooksRepository : CookbooksRepository {

		private val cookbook = CookbookSummary(
			id = TEST_COOKBOOK_ID,
			name = TEST_COOKBOOK_NAME,
			recipeCount = 1,
			updatedAtEpochMillis = 0L,
		)
		private var cookbookRecipes = listOf(
			RecipeSummary(
				id = TEST_RECIPE_ID,
				title = TEST_RECIPE_TITLE,
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 30,
				isFavorite = true,
			),
		)

		var removeRecipeFromCookbookCallCount: Int = 0
			private set

		override suspend fun getCookbooksPage(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> {
			val summary = cookbook.copy(recipeCount = cookbookRecipes.size)
			return Ok(
				CookbookListPage(
					items = listOf(summary),
					pageNumber = pageNumber,
					pageSize = pageSize,
					totalMatches = 1,
				),
			)
		}

		override suspend fun createCookbook(name: String): Outcome<CookbookSummary> = Ok(cookbook)

		override suspend fun deleteCookbook(cookbookId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun getCookbookRecipesPage(
			cookbookId: Int,
			pageNumber: Int,
			pageSize: Int,
		): Outcome<SearchResultsPage> {
			return Ok(
				SearchResultsPage(
					items = cookbookRecipes,
					pageNumber = pageNumber,
					pageSize = pageSize,
					totalMatches = cookbookRecipes.size,
				),
			)
		}

		override suspend fun addRecipeToCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> {
			removeRecipeFromCookbookCallCount += 1
			cookbookRecipes = cookbookRecipes.filterNot { it.id == recipeId }
			return Ok(Unit)
		}

		override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = Ok(emptyList())

		override fun observeCookbookMembershipEvents() = emptyFlow<CookbookMembershipEvent>()

		fun setCookbookRecipes(recipes: List<RecipeSummary>) {
			cookbookRecipes = recipes
		}
	}
}

private val testCookbookCoverImageUrl = GetCookbookCoverImageUrlUseCase(
	repository = object : CookbookCoverRepository {
		override fun getCookbookCoverImageUrl(
			cookbookId: Int,
			candidateImageUrls: List<String>,
			nowMillis: Long,
			random: kotlin.random.Random,
		): String? = candidateImageUrls.firstOrNull()
	},
)

private const val TEST_COOKBOOK_ID = 23
private const val TEST_COOKBOOK_NAME = "Weeknight dinners"

private fun favoritesViewModelForTest(
	favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
	cookbooksRepository: CookbooksRepository = FakeCookbooksRepository(),
	sessionKey: String? = "session",
): LibraryViewModel = LibraryViewModel(
	getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepository),
	getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
	createCookbook = CreateCookbookUseCase(cookbooksRepository),
	deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
	getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
	getCookbookCoverImageUrl = testCookbookCoverImageUrl,
	importCookbookShare = unusedImportCookbookShareUseCase(),
	observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	sessionKey = sessionKey,
)

private fun cookbookDetailViewModelForTest(
	cookbooksRepository: CookbooksRepository,
	favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
): CookbookDetailViewModel = CookbookDetailViewModel(
	getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
	removeRecipeFromCookbookUseCase = RemoveRecipeFromCookbookUseCase(cookbooksRepository),
	getCookbookCoverImageUrl = testCookbookCoverImageUrl,
	observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
	shareCookbook = unusedShareCookbookUseCase(),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	cookbookId = TEST_COOKBOOK_ID,
	initialName = TEST_COOKBOOK_NAME,
	sessionKey = "session",
)

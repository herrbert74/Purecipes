package app.purecipes.feature.favorites.ui

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookRef
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class FavoritesScreenTest {

	private val getCookbookCoverImageUrl = GetCookbookCoverImageUrlUseCase(
		repository = object : CookbookCoverRepository {
			override fun getCookbookCoverImageUrl(
				cookbookId: Int,
				candidateImageUrls: List<String>,
				nowMillis: Long,
				random: kotlin.random.Random,
			): String? = candidateImageUrls.firstOrNull()
		},
	)

	@Test
	fun favoritesScreenDoesNotRecomposeTitle() = runRecompositionTrackingUiTest {
		val favoritesRepo = FakeFavoritesRepository()
		val cookbooksRepo = FakeCookbooksRepository()
		setTrackedContent {
			PurecipesTheme {
				FavoritesScreen(
					getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepo),
					createCookbook = CreateCookbookUseCase(cookbooksRepo),
					deleteCookbook = DeleteCookbookUseCase(cookbooksRepo),
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
					refreshSignal = REFRESH_SIGNAL,
					sessionKey = "session",
					onRecipeSelect = {},
				)
			}
		}
		onNodeWithText("Favorites").assertIsDisplayed()
		onNodeWithTag(FAVORITES_TITLE_TAG).assertStable()
	}

	@Test
	fun cookbookDeleteButtonDisabledForNonEmptyCookbook() = runRecompositionTrackingUiTest {
		val favoritesRepo = FakeFavoritesRepository()
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
				FavoritesScreen(
					getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepo),
					createCookbook = CreateCookbookUseCase(cookbooksRepo),
					deleteCookbook = DeleteCookbookUseCase(cookbooksRepo),
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
					refreshSignal = REFRESH_SIGNAL,
					sessionKey = "session",
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		onAllNodesWithText("Delete")[0].assertIsNotEnabled()
	}

	@Test
	fun createCookbookInvokesCreateUseCase() = runRecompositionTrackingUiTest {
		val favoritesRepo = FakeFavoritesRepository()
		val cookbooksRepo = FakeCookbooksRepository()
		setTrackedContent {
			PurecipesTheme {
				FavoritesScreen(
					getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepo),
					createCookbook = CreateCookbookUseCase(cookbooksRepo),
					deleteCookbook = DeleteCookbookUseCase(cookbooksRepo),
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
					refreshSignal = REFRESH_SIGNAL,
					sessionKey = "session",
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
		val favoritesRepo = FakeFavoritesRepository()
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
				FavoritesScreen(
					getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepo),
					createCookbook = CreateCookbookUseCase(cookbooksRepo),
					deleteCookbook = DeleteCookbookUseCase(cookbooksRepo),
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
					refreshSignal = REFRESH_SIGNAL,
					sessionKey = "session",
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		onNodeWithTag("$DELETE_COOKBOOK_BUTTON_PREFIX$EMPTY_COOKBOOK_ID").performClick()
		onNodeWithText("Delete cookbook?").assertIsDisplayed()
		onNodeWithTag(DELETE_COOKBOOK_DIALOG_CONFIRM_TAG).performClick()

		waitForIdle()
		assertEquals(1, cookbooksRepo.deleteCookbookCallCount)
	}

	@Test
	fun cookbookDetailRefreshRemovesRecipeAfterFavoritesRefreshSignal() = runRecompositionTrackingUiTest {
		val favoritesRepo = FakeFavoritesRepository()
		val cookbooksRepo = MutableCookbooksRepository()
		val refreshSignalState = mutableIntStateOf(REFRESH_SIGNAL)
		setTrackedContent {
			PurecipesTheme {
				FavoritesScreen(
					getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepo),
					createCookbook = CreateCookbookUseCase(cookbooksRepo),
					deleteCookbook = DeleteCookbookUseCase(cookbooksRepo),
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					getCookbookCoverImageUrl = getCookbookCoverImageUrl,
					refreshSignal = refreshSignalState.intValue,
					sessionKey = "session",
					onRecipeSelect = {},
				)
			}
		}

		onNodeWithText("Cookbooks").performClick()
		onNodeWithText(TEST_COOKBOOK_NAME).performClick()
		onNodeWithText(TEST_RECIPE_TITLE).assertIsDisplayed()
		onNodeWithText("1 recipes").assertIsDisplayed()

		runOnIdle {
			cookbooksRepo.setCookbookRecipes(emptyList())
			refreshSignalState.intValue += 1
		}

		waitForIdle()
		onNodeWithText("Cookbooks").performClick()
		onNodeWithText(TEST_COOKBOOK_NAME).performClick()
		onAllNodesWithText(TEST_RECIPE_TITLE).assertCountEquals(0)
	}

	private companion object {

		const val REFRESH_SIGNAL = 1
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

		override suspend fun removeRecipeFromCookbook(cookbookId: Int, recipeId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun getRecipeCookbooks(recipeId: Int): Outcome<List<CookbookRef>> = Ok(emptyList())

		fun setCookbookRecipes(recipes: List<RecipeSummary>) {
			cookbookRecipes = recipes
		}
	}
}

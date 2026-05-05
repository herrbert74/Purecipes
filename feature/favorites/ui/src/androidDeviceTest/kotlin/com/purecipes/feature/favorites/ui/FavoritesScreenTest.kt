package com.purecipes.feature.favorites.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.shared.domain.model.CookbookSummary
import com.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import com.purecipes.shared.ui.theme.PurecipesTheme
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
		repository = object : com.purecipes.feature.favorites.domain.repository.CookbookCoverRepository {
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
			cookbooksPageResult = com.github.michaelbull.result.Ok(
				com.purecipes.shared.domain.model.CookbookListPage(
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
			cookbooksPageResult = com.github.michaelbull.result.Ok(
				com.purecipes.shared.domain.model.CookbookListPage(
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

	private companion object {
		const val REFRESH_SIGNAL = 1
		const val NON_EMPTY_COOKBOOK_ID = 21
		const val EMPTY_COOKBOOK_ID = 22
	}
}

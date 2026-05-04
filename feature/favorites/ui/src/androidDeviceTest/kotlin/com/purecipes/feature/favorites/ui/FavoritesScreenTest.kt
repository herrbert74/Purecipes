package com.purecipes.feature.favorites.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import com.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import com.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.assertStable
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class FavoritesScreenTest {

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
					getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepo),
					refreshSignal = 1,
					sessionKey = "session",
					onRecipeSelect = {},
				)
			}
		}
		onNodeWithText("Favorites").assertIsDisplayed()
		onNodeWithTag(FAVORITES_TITLE_TAG).assertStable()
	}
}

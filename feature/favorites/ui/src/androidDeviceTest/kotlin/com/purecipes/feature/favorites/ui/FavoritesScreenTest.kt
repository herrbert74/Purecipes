package com.purecipes.feature.favorites.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
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
		val repos = FakeFavoritesRepository()
		setTrackedContent {
			PurecipesTheme {
				FavoritesScreen(
					getFavoriteRecipes = GetFavoriteRecipesUseCase(repos),
					refreshSignal = 1,
					sessionKey = "session",
					onRecipeSelect = {}
				)
			}
		}
		onNodeWithText("Favorites").assertIsDisplayed()
		onNodeWithTag(FAVORITES_TITLE_TAG).assertStable()
	}
}

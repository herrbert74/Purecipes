package app.purecipes.feature.home.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.github.michaelbull.result.Ok
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class HomeShelfScreenTest {

	@Test
	fun shelfScreenShowsTheTitleAndOpensARecipe() = runRecompositionTrackingUiTest {
		val recipe = RecipeSummary(
			id = 7,
			title = "Sunday roast",
			cuisine = null,
			imageUrl = null,
			totalTime = 90,
		)
		val homeFeedRepository = FakeHomeFeedRepository()
		homeFeedRepository.collectionPages = mapOf(
			"this-week" to Ok(
				SearchResultsPage(
					items = listOf(recipe),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = HomeShelfViewModel(
			getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			shelfId = HomeShelfId.COLLECTION,
			collectionSlug = "this-week",
			habitCuisine = null,
			title = "This week",
			sessionKey = null,
		)
		var selectedRecipeId by mutableStateOf<Int?>(null)
		var wentBack by mutableStateOf(false)
		setTrackedContent {
			PurecipesTheme {
				HomeShelfScreen(
					title = "This week",
					shelfId = HomeShelfId.COLLECTION,
					collectionSlug = "this-week",
					onBack = { wentBack = true },
					onRecipeSelect = { recipeId, _ -> selectedRecipeId = recipeId },
					viewModel = viewModel,
				)
			}
		}

		onNodeWithText("This week").assertIsDisplayed()
		onNodeWithText("Sunday roast").assertIsDisplayed()
		onNodeWithText("Sunday roast").performClick()
		onNodeWithContentDescription("Navigate up").performClick()

		assertEquals(recipe.id, selectedRecipeId)
		assertTrue(wentBack)
	}
}

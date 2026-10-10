package app.purecipes.feature.home.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class HomeFeatureRequestCardTest {

	@Test
	fun votedRequestCardKeepsDescriptionVoteCountAndAction() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				HomeFeatureRequestCard(
					featureRequest = FeatureRequest(
						id = 1,
						title = "Meal plan for the week",
						description = "Plan dinners ahead",
						status = FeatureRequestStatus.OPEN,
						voteCount = 12,
						commentCount = 2,
						createdAtEpochMillis = 0L,
						votedByCurrentUser = true,
					),
					onOpen = {},
				)
			}
		}

		onNodeWithText("Feature request").assertIsDisplayed()
		onNodeWithText("Meal plan for the week").assertIsDisplayed()
		onNodeWithText("Plan dinners ahead").assertIsDisplayed()
		onNodeWithText("12 votes").assertIsDisplayed()
		onNodeWithTag(HOME_FEATURE_REQUEST_VOTE_TAG).assertIsDisplayed()
		onNodeWithText("Vote").assertIsDisplayed()
	}

	@Test
	fun suggestCardStaysExpandedWithDescriptionAndAction() = runRecompositionTrackingUiTest {
		var opened = false
		setTrackedContent {
			PurecipesTheme {
				HomeFeatureRequestCard(
					featureRequest = null,
					onOpen = { opened = true },
				)
			}
		}

		onNodeWithText("Suggest a feature").assertIsDisplayed()
		onNodeWithText(
			"Tell us what would make Purecipes better and other cooks can vote for it.",
		).assertIsDisplayed()
		onNodeWithTag(HOME_FEATURE_REQUEST_SUGGEST_TAG).assertIsDisplayed()
		onNodeWithText("Suggest").performClick()
		assertEquals(true, opened)
	}
}

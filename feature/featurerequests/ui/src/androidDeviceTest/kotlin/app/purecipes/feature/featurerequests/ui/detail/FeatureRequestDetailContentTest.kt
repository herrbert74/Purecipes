package app.purecipes.feature.featurerequests.ui.detail

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.featurerequests.ui.FEATURE_REQUEST_VOTE_BUTTON_TAG_PREFIX
import app.purecipes.feature.featurerequests.ui.FeatureRequestDetailCallbacks
import app.purecipes.feature.featurerequests.ui.FeatureRequestDetailUiState
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class FeatureRequestDetailContentTest {

	@Test
	fun detailShowsRequestStatusAndComments() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestDetailContent(
					featureRequest = featureRequest(),
					comments = persistentListOf(comment()),
					state = detailState(),
					callbacks = FeatureRequestDetailCallbacks(
						onBack = {},
						onToggleVote = {},
						onAddComment = {},
					),
				)
			}
		}

		onNodeWithTag(FEATURE_REQUEST_DETAIL_TITLE_TAG).assertIsDisplayed()
		onNodeWithText("In progress", useUnmergedTree = true).assertIsDisplayed()
		onNodeWithText("Comments (1)").assertIsDisplayed()
		onNodeWithText(COMMENT_BODY).assertIsDisplayed()
	}

	@Test
	fun upvoteButtonReportsTheToggle() = runRecompositionTrackingUiTest {
		var toggled = false
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestDetailContent(
					featureRequest = featureRequest(),
					comments = persistentListOf(),
					state = detailState(),
					callbacks = FeatureRequestDetailCallbacks(
						onBack = {},
						onToggleVote = { toggled = true },
						onAddComment = {},
					),
				)
			}
		}

		onNodeWithTag("${FEATURE_REQUEST_VOTE_BUTTON_TAG_PREFIX}detail").performClick()

		assertEquals(true, toggled)
	}

	@Test
	fun composerSendsTheTypedComment() = runRecompositionTrackingUiTest {
		var submitted: String? = null
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestDetailContent(
					featureRequest = featureRequest(),
					comments = persistentListOf(),
					state = detailState(),
					callbacks = FeatureRequestDetailCallbacks(
						onBack = {},
						onToggleVote = {},
						onAddComment = { submitted = it },
					),
				)
			}
		}

		onNodeWithTag(FEATURE_REQUEST_COMMENT_INPUT_TAG).performTextInput("Ship it")
		onNodeWithTag(FEATURE_REQUEST_COMMENT_SEND_TAG).performClick()

		assertEquals("Ship it", submitted)
	}

	private fun featureRequest(): FeatureRequest = FeatureRequest(
		id = 1,
		title = "Shopping list from a recipe",
		description = "Turn the ingredient list into a shopping list.",
		status = FeatureRequestStatus.IN_PROGRESS,
		voteCount = 42,
		commentCount = 1,
		createdAtEpochMillis = 0L,
		votedByCurrentUser = true,
	)

	private fun comment(): FeatureRequestComment = FeatureRequestComment(
		id = 1,
		requestId = 1,
		authorDisplayName = "Ada",
		body = COMMENT_BODY,
		createdAtEpochMillis = 0L,
	)

	private fun detailState(): FeatureRequestDetailUiState = FeatureRequestDetailUiState(
		isLoading = false,
		isSendingComment = false,
		errorMessage = null,
		commentErrorMessage = null,
	)

	private companion object {

		const val COMMENT_BODY = "Grouping by aisle would be great."
	}
}

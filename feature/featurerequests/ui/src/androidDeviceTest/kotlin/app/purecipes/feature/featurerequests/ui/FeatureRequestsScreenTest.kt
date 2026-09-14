package app.purecipes.feature.featurerequests.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.featurerequests.domain.model.FeatureRequestEvent
import app.purecipes.feature.featurerequests.domain.usecase.CreateFeatureRequestUseCase
import app.purecipes.feature.featurerequests.domain.usecase.GetFeatureRequestsPageUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ObserveFeatureRequestEventsUseCase
import app.purecipes.feature.featurerequests.domain.usecase.ToggleFeatureRequestVoteUseCase
import app.purecipes.shared.testfixtures.fake.FakeFeatureRequestsRepository
import app.purecipes.shared.testfixtures.fake.fakeFeatureRequest
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class FeatureRequestsScreenTest {

	@Test
	fun listUpdatesCommentCountAfterCommentAddedEvent() = runRecompositionTrackingUiTest {
		val request = fakeFeatureRequest(id = REQUEST_ID, title = "Dark mode", voteCount = 6, commentCount = 0)
		val repository = FakeFeatureRequestsRepository(initialFeatureRequests = listOf(request))
		val viewModel = FeatureRequestsViewModel(
			getFeatureRequestsPage = GetFeatureRequestsPageUseCase(repository),
			createFeatureRequest = CreateFeatureRequestUseCase(repository),
			toggleFeatureRequestVote = ToggleFeatureRequestVoteUseCase(repository),
			observeFeatureRequestEvents = ObserveFeatureRequestEventsUseCase(repository),
			sessionKey = "session",
		)
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsScreen(
					onBack = {},
					onFeatureRequestSelect = {},
					sessionKey = "session",
					viewModel = viewModel,
				)
			}
		}

		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithText("Dark mode").fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithText("Dark mode").assertIsDisplayed()
		val commentCountTag = "$FEATURE_REQUEST_COMMENT_COUNT_TAG_PREFIX$REQUEST_ID"
		onNodeWithTag(commentCountTag, useUnmergedTree = true).assertTextEquals("0")

		runOnIdle {
			repository.emitFeatureRequestEvent(FeatureRequestEvent.CommentAdded(requestId = REQUEST_ID))
		}

		waitUntil(timeoutMillis = 5_000) {
			onAllNodesWithText("1").fetchSemanticsNodes().isNotEmpty()
		}
		onNodeWithTag(commentCountTag, useUnmergedTree = true).assertTextEquals("1")
	}

	private companion object {

		const val REQUEST_ID = 4
	}
}

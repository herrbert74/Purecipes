package app.purecipes.feature.featurerequests.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class FeatureRequestsListContentTest {

	@Test
	fun listShowsTitlesVoteCountsAndStatusBadges() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsListContent(
					state = listState(),
					paginationState = loadedPaginationState(),
					callbacks = FeatureRequestsListCallbacks(
						onSortSelected = {},
						onStatusFilterSelected = {},
						onFeatureRequestSelect = {},
						onToggleVote = {},
						onCreateClick = {},
					),
				)
			}
		}

		onNodeWithText(PLANNED_TITLE).performScrollTo().assertIsDisplayed()
		onNodeWithText(OPEN_TITLE).performScrollTo().assertIsDisplayed()
		onNodeWithText("42").assertIsDisplayed()
		onNodeWithTag("$FEATURE_REQUEST_ROW_TAG_PREFIX$PLANNED_ID").assertIsDisplayed()
		onNodeWithTag(FEATURE_REQUEST_SORT_CHIP_TAG_PREFIX + FeatureRequestSort.TOP_VOTES.name)
			.assertIsDisplayed()
	}

	@Test
	fun sortAndStatusChipsReportSelection() = runRecompositionTrackingUiTest {
		var selectedSort: FeatureRequestSort? = null
		var selectedStatus: FeatureRequestStatus? = null
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsListContent(
					state = listState(),
					paginationState = loadedPaginationState(),
					callbacks = FeatureRequestsListCallbacks(
						onSortSelected = { selectedSort = it },
						onStatusFilterSelected = { selectedStatus = it },
						onFeatureRequestSelect = {},
						onToggleVote = {},
						onCreateClick = {},
					),
				)
			}
		}

		onNodeWithTag(FEATURE_REQUEST_SORT_CHIP_TAG_PREFIX + FeatureRequestSort.NEWEST.name)
			.performScrollTo()
			.performClick()
		onNodeWithTag(FEATURE_REQUEST_STATUS_CHIP_TAG_PREFIX + FeatureRequestStatus.DONE.name)
			.performScrollTo()
			.performClick()
		waitForIdle()

		assertEquals(FeatureRequestSort.NEWEST, selectedSort)
		assertEquals(FeatureRequestStatus.DONE, selectedStatus)
	}

	@Test
	fun rowClickAndVoteButtonReportTheFeatureRequest() = runRecompositionTrackingUiTest {
		var selectedId: Int? = null
		var votedId: Int? = null
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsListContent(
					state = listState(),
					paginationState = loadedPaginationState(),
					callbacks = FeatureRequestsListCallbacks(
						onSortSelected = {},
						onStatusFilterSelected = {},
						onFeatureRequestSelect = { selectedId = it },
						onToggleVote = { votedId = it.id },
						onCreateClick = {},
					),
				)
			}
		}

		onNodeWithTag("$FEATURE_REQUEST_VOTE_BUTTON_TAG_PREFIX$PLANNED_ID").performScrollTo().performClick()
		onNodeWithText(PLANNED_TITLE).performScrollTo().performClick()

		assertEquals(PLANNED_ID, votedId)
		assertEquals(PLANNED_ID, selectedId)
	}

	@Test
	fun emptyStateOffersCreatingARequest() = runRecompositionTrackingUiTest {
		var createClicked = false
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsListContent(
					state = FeatureRequestsListState(
						featureRequests = persistentListOf(),
						sort = FeatureRequestSort.TOP_VOTES,
						statusFilter = null,
						totalMatches = 0,
						errorMessage = null,
						isLoading = false,
					),
					paginationState = PaginationState(initialPageKey = 1, onRequestPage = {}),
					callbacks = FeatureRequestsListCallbacks(
						onSortSelected = {},
						onStatusFilterSelected = {},
						onFeatureRequestSelect = {},
						onToggleVote = {},
						onCreateClick = { createClicked = true },
					),
				)
			}
		}

		onNodeWithText("No requests yet").assertIsDisplayed()
		onNodeWithText("Tell us what would make Purecipes better and other cooks can vote for it.")
			.assertIsDisplayed()
		onNodeWithText("Request a feature").performClick()

		assertEquals(true, createClicked)
	}

	@Test
	fun filteredEmptyStateExplainsMissingRequestsInThatStage() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				FeatureRequestsListContent(
					state = FeatureRequestsListState(
						featureRequests = persistentListOf(),
						sort = FeatureRequestSort.TOP_VOTES,
						statusFilter = FeatureRequestStatus.PLANNED,
						totalMatches = 0,
						errorMessage = null,
						isLoading = false,
					),
					paginationState = PaginationState(initialPageKey = 1, onRequestPage = {}),
					callbacks = FeatureRequestsListCallbacks(
						onSortSelected = {},
						onStatusFilterSelected = {},
						onFeatureRequestSelect = {},
						onToggleVote = {},
						onCreateClick = {},
					),
				)
			}
		}

		onNodeWithText("Nothing in this stage yet").assertIsDisplayed()
		onNodeWithText("Nothing has made it into Planned at the moment.").assertIsDisplayed()
		onNodeWithText("No requests yet").assertDoesNotExist()
		onNodeWithText("Request a feature").assertDoesNotExist()
	}

	private fun listState(): FeatureRequestsListState = FeatureRequestsListState(
		featureRequests = persistentListOf(
			FeatureRequest(
				id = PLANNED_ID,
				title = PLANNED_TITLE,
				description = "Turn the ingredient list into a shopping list.",
				status = FeatureRequestStatus.PLANNED,
				voteCount = 42,
				commentCount = 3,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = true,
			),
			FeatureRequest(
				id = 2,
				title = OPEN_TITLE,
				description = "Keep the screen readable in the evening.",
				status = FeatureRequestStatus.OPEN,
				voteCount = 7,
				commentCount = 0,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = false,
			),
		),
		sort = FeatureRequestSort.TOP_VOTES,
		statusFilter = null,
		totalMatches = 2,
		errorMessage = null,
		isLoading = false,
	)

	private fun loadedPaginationState(): PaginationState<Int, FeatureRequest> {
		val paginationState = PaginationState<Int, FeatureRequest>(
			initialPageKey = 1,
			onRequestPage = {},
		)
		paginationState.appendPage(
			pageKey = 1,
			items = listState().featureRequests,
			nextPageKey = 2,
			isLastPage = true,
		)
		return paginationState
	}

	private companion object {

		const val PLANNED_ID = 1
		const val PLANNED_TITLE = "Shopping list from a recipe"
		const val OPEN_TITLE = "Dark mode for the cooking screen"
	}
}

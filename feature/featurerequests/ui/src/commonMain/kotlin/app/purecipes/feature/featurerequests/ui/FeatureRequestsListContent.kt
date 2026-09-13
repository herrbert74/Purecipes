package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.paging.PaginatedLazyColumn
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun FeatureRequestsListContent(
	state: FeatureRequestsListState,
	paginationState: PaginationState<Int, FeatureRequest>,
	callbacks: FeatureRequestsListCallbacks,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxSize()) {
		FeatureRequestFilters(
			sort = state.sort,
			statusFilter = state.statusFilter,
			onSortSelect = callbacks.onSortSelected,
			onStatusFilterSelect = callbacks.onStatusFilterSelected,
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.m,
				vertical = PurecipesTheme.space.s,
			),
		)
		state.errorMessage?.let { message ->
			ErrorText(
				text = message,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
			)
		}
		if (state.isLoading && state.featureRequests.isEmpty()) {
			Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
		} else if (state.featureRequests.isEmpty() && state.totalMatches == 0) {
			Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
				EmptyStateContent(
					icon = Icons.Filled.Lightbulb,
					iconContentDescription = "Feature requests",
					title = "No requests yet",
					description = "Tell us what would make Purecipes better and other cooks can vote for it.",
					action = {
						PurecipesButton(
							text = "Request a feature",
							onClick = callbacks.onCreateClick,
						)
					},
				)
			}
		} else {
			PaginatedLazyColumn(
				paginationState = paginationState,
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				contentPadding = PaddingValues(
					start = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					bottom = PurecipesTheme.space.xxl,
				),
			) {
				items(state.featureRequests.size, key = { state.featureRequests[it].id }) { index ->
					val featureRequest = state.featureRequests[index]
					FeatureRequestRow(
						featureRequest = featureRequest,
						onClick = { callbacks.onFeatureRequestSelect(featureRequest.id) },
						onToggleVote = { callbacks.onToggleVote(featureRequest) },
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestsListContentPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestsListContent(
			state = FeatureRequestsListState(
				featureRequests = previewFeatureRequests(),
				sort = FeatureRequestSort.TOP_VOTES,
				statusFilter = null,
				totalMatches = 2,
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

private fun previewFeatureRequests(): ImmutableList<FeatureRequest> = persistentListOf(
	FeatureRequest(
		id = 1,
		title = "Shopping list from a recipe",
		description = "Turn the ingredient list into a shopping list.",
		status = FeatureRequestStatus.PLANNED,
		voteCount = 42,
		commentCount = 3,
		createdAtEpochMillis = 0L,
		votedByCurrentUser = true,
	),
	FeatureRequest(
		id = 2,
		title = "Dark mode for the cooking screen",
		description = "Keep the screen readable when cooking in the evening.",
		status = FeatureRequestStatus.OPEN,
		voteCount = 7,
		commentCount = 0,
		createdAtEpochMillis = 0L,
		votedByCurrentUser = false,
	),
)

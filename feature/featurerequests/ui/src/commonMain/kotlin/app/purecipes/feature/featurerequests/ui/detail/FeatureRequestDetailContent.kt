package app.purecipes.feature.featurerequests.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.featurerequests.ui.FeatureRequestDetailCallbacks
import app.purecipes.feature.featurerequests.ui.FeatureRequestDetailUiState
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal const val FEATURE_REQUEST_DETAIL_TITLE_TAG = "featureRequestDetailTitle"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeatureRequestDetailContent(
	featureRequest: FeatureRequest?,
	comments: ImmutableList<FeatureRequestComment>,
	state: FeatureRequestDetailUiState,
	callbacks: FeatureRequestDetailCallbacks,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				title = { Text(text = "Feature request") },
				navigationIcon = {
					IconButton(onClick = callbacks.onBack) {
						Icon(
							imageVector = Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = "Back",
						)
					}
				},
			)
		},
	) { innerPadding ->
		if (featureRequest == null) {
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				val errorMessage = state.errorMessage
				if (errorMessage == null) {
					CircularProgressIndicator()
				} else {
					ErrorText(text = errorMessage, textAlign = TextAlign.Center)
				}
			}
			return@Scaffold
		}

		Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
			LazyColumn(
				modifier = Modifier.weight(1f),
				contentPadding = PaddingValues(PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				item {
					FeatureRequestDetailHeader(
						featureRequest = featureRequest,
						onToggleVote = callbacks.onToggleVote,
					)
				}
				item {
					HorizontalDivider()
				}
				item {
					Text(
						text = "Comments (${featureRequest.commentCount})",
						style = PurecipesTheme.typography.titleMedium,
						modifier = Modifier.fillMaxWidth(),
					)
				}
				items(comments, key = { it.id }) { comment ->
					FeatureRequestCommentRow(comment = comment)
				}
			}
			state.commentErrorMessage?.let { message ->
				ErrorText(
					text = message,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.m),
				)
			}
			FeatureRequestCommentComposer(
				isSending = state.isSendingComment,
				onSubmit = callbacks.onAddComment,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestDetailContentPreview() {
	PurecipesTheme(darkTheme = false) {
		FeatureRequestDetailContent(
			featureRequest = FeatureRequest(
				id = 1,
				title = "Shopping list from a recipe",
				description = "Let me turn the ingredient list into a shopping list I can take to the shop.",
				status = FeatureRequestStatus.IN_PROGRESS,
				voteCount = 42,
				commentCount = 1,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = true,
			),
			comments = persistentListOf(
				FeatureRequestComment(
					id = 1,
					requestId = 1,
					authorDisplayName = "Ada",
					body = "Grouping by aisle would be great.",
					createdAtEpochMillis = 0L,
				),
			),
			state = FeatureRequestDetailUiState(
				isLoading = false,
				isSendingComment = false,
				errorMessage = null,
				commentErrorMessage = null,
			),
			callbacks = FeatureRequestDetailCallbacks(
				onBack = {},
				onToggleVote = {},
				onAddComment = {},
			),
		)
	}
}

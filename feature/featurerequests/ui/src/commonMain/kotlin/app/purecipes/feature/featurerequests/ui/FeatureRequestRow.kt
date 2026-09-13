package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUEST_ROW_TAG_PREFIX = "featureRequestRow:"

private const val FEATURE_REQUEST_DESCRIPTION_MAX_LINES = 2

@Composable
internal fun FeatureRequestRow(
	featureRequest: FeatureRequest,
	onClick: () -> Unit,
	onToggleVote: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.testTag("$FEATURE_REQUEST_ROW_TAG_PREFIX${featureRequest.id}"),
	) {
		Row(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
		) {
			FeatureRequestVoteButton(
				voteCount = featureRequest.voteCount,
				isVoted = featureRequest.votedByCurrentUser,
				testTagSuffix = featureRequest.id.toString(),
				onToggleVote = onToggleVote,
			)
			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				Text(
					text = featureRequest.title,
					style = PurecipesTheme.typography.titleMedium,
					fontWeight = FontWeight.SemiBold,
				)
				Text(
					text = featureRequest.description,
					style = PurecipesTheme.typography.bodySmall,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					maxLines = FEATURE_REQUEST_DESCRIPTION_MAX_LINES,
					overflow = TextOverflow.Ellipsis,
				)
				Row(
					horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
					verticalAlignment = Alignment.CenterVertically,
				) {
					FeatureRequestStatusBadge(status = featureRequest.status)
					Icon(
						imageVector = Icons.Outlined.ChatBubbleOutline,
						contentDescription = "Comments",
						tint = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = featureRequest.commentCount.toString(),
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestRowPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestRow(
			featureRequest = FeatureRequest(
				id = 1,
				title = "Shopping list from a recipe",
				description = "Let me turn the ingredient list into a shopping list I can take to the shop.",
				status = FeatureRequestStatus.PLANNED,
				voteCount = 42,
				commentCount = 3,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = true,
			),
			onClick = {},
			onToggleVote = {},
		)
	}
}

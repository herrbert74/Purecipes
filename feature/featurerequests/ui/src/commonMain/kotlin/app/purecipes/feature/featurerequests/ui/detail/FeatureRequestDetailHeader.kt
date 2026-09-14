package app.purecipes.feature.featurerequests.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.featurerequests.ui.FeatureRequestStatusBadge
import app.purecipes.feature.featurerequests.ui.FeatureRequestVoteButton
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun FeatureRequestDetailHeader(
	featureRequest: FeatureRequest,
	onToggleVote: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		FeatureRequestVoteButton(
			voteCount = featureRequest.voteCount,
			isVoted = featureRequest.votedByCurrentUser,
			testTagSuffix = "detail",
			onToggleVote = onToggleVote,
		)
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = featureRequest.title,
				style = PurecipesTheme.typography.headlineSmall,
				fontWeight = FontWeight.SemiBold,
				modifier = Modifier.testTag(FEATURE_REQUEST_DETAIL_TITLE_TAG),
			)
			FeatureRequestStatusBadge(status = featureRequest.status)
			Text(
				text = featureRequest.description,
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestDetailHeaderPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestDetailHeader(
			featureRequest = FeatureRequest(
				id = 1,
				title = "Shopping list from a recipe",
				description = "Let me turn the ingredient list into a shopping list.",
				status = FeatureRequestStatus.PLANNED,
				voteCount = 42,
				commentCount = 1,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = false,
			),
			onToggleVote = {},
		)
	}
}

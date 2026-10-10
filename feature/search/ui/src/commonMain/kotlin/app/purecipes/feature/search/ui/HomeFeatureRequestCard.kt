package app.purecipes.feature.search.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val HOME_FEATURE_REQUEST_CARD_TAG = "homeFeatureRequestCard"
internal const val HOME_FEATURE_REQUEST_VOTE_TAG = "homeFeatureRequestVote"
internal const val HOME_FEATURE_REQUEST_SUGGEST_TAG = "homeFeatureRequestSuggest"

private const val HOME_FEATURE_REQUEST_LABEL = "Feature request"
private const val HOME_FEATURE_REQUEST_VOTE_LABEL = "Vote"
private const val HOME_SUGGEST_FEATURE_TITLE = "Suggest a feature"
private const val HOME_SUGGEST_FEATURE_DESCRIPTION =
	"Tell us what would make Purecipes better and other cooks can vote for it."
private const val HOME_SUGGEST_FEATURE_ACTION = "Suggest"

@Composable
internal fun HomeFeatureRequestCard(
	featureRequest: FeatureRequest?,
	onOpen: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onOpen,
		modifier = modifier
			.fillMaxWidth()
			.testTag(HOME_FEATURE_REQUEST_CARD_TAG),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
		),
	) {
		val description = featureRequest?.description ?: HOME_SUGGEST_FEATURE_DESCRIPTION
		val actionLabel = if (featureRequest == null) {
			HOME_SUGGEST_FEATURE_ACTION
		} else {
			HOME_FEATURE_REQUEST_VOTE_LABEL
		}
		val actionTag = if (featureRequest == null) {
			HOME_FEATURE_REQUEST_SUGGEST_TAG
		} else {
			HOME_FEATURE_REQUEST_VOTE_TAG
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				if (featureRequest == null) {
					Text(
						text = HOME_SUGGEST_FEATURE_TITLE,
						style = PurecipesTheme.typography.titleMedium,
						color = PurecipesTheme.colorScheme.onSurface,
					)
				} else {
					Text(
						text = HOME_FEATURE_REQUEST_LABEL,
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
					Text(
						text = featureRequest.title,
						style = PurecipesTheme.typography.titleMedium,
						color = PurecipesTheme.colorScheme.onSurface,
					)
				}
				Text(
					text = description,
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
				if (featureRequest != null) {
					Text(
						text = "${featureRequest.voteCount} votes",
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
			}
			TextButton(
				onClick = onOpen,
				modifier = Modifier.testTag(actionTag),
			) {
				Text(text = actionLabel)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun HomeFeatureRequestCardPreview() {
	PurecipesPreviewScaffold {
		HomeFeatureRequestCard(
			featureRequest = FeatureRequest(
				id = 1,
				title = "Meal plan for the week",
				description = "Plan dinners ahead",
				status = FeatureRequestStatus.OPEN,
				voteCount = 12,
				commentCount = 2,
				createdAtEpochMillis = 0L,
				votedByCurrentUser = false,
			),
			onOpen = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun HomeFeatureRequestSuggestPreview() {
	PurecipesPreviewScaffold {
		HomeFeatureRequestCard(
			featureRequest = null,
			onOpen = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

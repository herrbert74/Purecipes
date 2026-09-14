package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUEST_VOTE_BUTTON_TAG_PREFIX = "featureRequestVoteButton:"

private val FEATURE_REQUEST_VOTE_BUTTON_WIDTH = 56.dp

@Composable
internal fun FeatureRequestVoteButton(
	voteCount: Int,
	isVoted: Boolean,
	testTagSuffix: String,
	onToggleVote: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier
			.width(FEATURE_REQUEST_VOTE_BUTTON_WIDTH)
			.clickable(onClick = onToggleVote)
			.testTag("$FEATURE_REQUEST_VOTE_BUTTON_TAG_PREFIX$testTagSuffix"),
		shape = PurecipesTheme.shapes.medium,
		color = if (isVoted) {
			PurecipesTheme.colorScheme.primaryContainer
		} else {
			PurecipesTheme.colorScheme.surfaceVariant
		},
		contentColor = if (isVoted) {
			PurecipesTheme.colorScheme.onPrimaryContainer
		} else {
			PurecipesTheme.colorScheme.onSurfaceVariant
		},
	) {
		Column(
			modifier = Modifier.padding(vertical = PurecipesTheme.space.s),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.quark),
		) {
			Icon(
				imageVector = Icons.Filled.KeyboardArrowUp,
				contentDescription = if (isVoted) "Remove upvote" else "Upvote",
			)
			Text(
				text = voteCount.toString(),
				style = PurecipesTheme.typography.titleMedium,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestVoteButtonPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestVoteButton(
			voteCount = 12,
			isVoted = true,
			testTagSuffix = "1",
			onToggleVote = {},
		)
	}
}

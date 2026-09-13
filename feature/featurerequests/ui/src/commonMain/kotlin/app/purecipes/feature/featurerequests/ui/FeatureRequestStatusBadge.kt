package app.purecipes.feature.featurerequests.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FEATURE_REQUEST_STATUS_BADGE_TAG_PREFIX = "featureRequestStatusBadge:"

@Composable
internal fun FeatureRequestStatusBadge(
	status: FeatureRequestStatus,
	modifier: Modifier = Modifier,
) {
	val containerColor = when (status) {
		FeatureRequestStatus.OPEN -> PurecipesTheme.colorScheme.surfaceVariant
		FeatureRequestStatus.PLANNED -> PurecipesTheme.colorScheme.secondaryContainer
		FeatureRequestStatus.IN_PROGRESS -> PurecipesTheme.colorScheme.tertiaryContainer
		FeatureRequestStatus.DONE -> PurecipesTheme.colorScheme.primaryContainer
	}
	val contentColor = when (status) {
		FeatureRequestStatus.OPEN -> PurecipesTheme.colorScheme.onSurfaceVariant
		FeatureRequestStatus.PLANNED -> PurecipesTheme.colorScheme.onSecondaryContainer
		FeatureRequestStatus.IN_PROGRESS -> PurecipesTheme.colorScheme.onTertiaryContainer
		FeatureRequestStatus.DONE -> PurecipesTheme.colorScheme.onPrimaryContainer
	}
	Surface(
		modifier = modifier.testTag("$FEATURE_REQUEST_STATUS_BADGE_TAG_PREFIX${status.name}"),
		shape = PurecipesTheme.shapes.small,
		color = containerColor,
		contentColor = contentColor,
	) {
		Text(
			text = status.label(),
			style = PurecipesTheme.typography.labelMedium,
			modifier = Modifier.padding(
				horizontal = PurecipesTheme.space.s,
				vertical = PurecipesTheme.space.xs,
			),
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestStatusBadgePreview() {
	PurecipesPreviewScaffold {
		Row(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
			FeatureRequestStatus.entries.forEach { status ->
				FeatureRequestStatusBadge(status = status)
			}
		}
	}
}

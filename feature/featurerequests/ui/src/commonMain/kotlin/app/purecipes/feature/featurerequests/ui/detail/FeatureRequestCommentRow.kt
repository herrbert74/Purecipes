package app.purecipes.feature.featurerequests.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun FeatureRequestCommentRow(
	comment: FeatureRequestComment,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.medium,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
		) {
			Text(
				text = comment.authorDisplayName,
				style = PurecipesTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
			Text(
				text = comment.body,
				style = PurecipesTheme.typography.bodyMedium,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun FeatureRequestCommentRowPreview() {
	PurecipesPreviewScaffold {
		FeatureRequestCommentRow(
			comment = FeatureRequestComment(
				id = 1,
				requestId = 1,
				authorDisplayName = "Ada",
				body = "Grouping the shopping list by aisle would be great.",
				createdAtEpochMillis = 0L,
			),
		)
	}
}

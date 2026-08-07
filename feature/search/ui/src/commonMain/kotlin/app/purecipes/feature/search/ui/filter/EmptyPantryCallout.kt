package app.purecipes.feature.search.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val FILTER_KEY_INGREDIENTS_EMPTY_PANTRY_TAG = "filterKeyIngredientsEmptyPantry"
internal const val FILTER_KEY_INGREDIENTS_GO_TO_PANTRY_TAG = "filterKeyIngredientsGoToPantry"

@Composable
internal fun EmptyPantryCallout(
	onGoToPantry: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.testTag(FILTER_KEY_INGREDIENTS_EMPTY_PANTRY_TAG)
			.padding(horizontal = PurecipesTheme.space.xl),
		shape = RoundedCornerShape(PurecipesTheme.space.s),
		color = PurecipesTheme.colorScheme.surfaceVariant,
	) {
		Row(
			modifier = Modifier.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.s,
				top = PurecipesTheme.space.s,
				bottom = PurecipesTheme.space.xs,
			),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Icon(
				imageVector = Icons.Default.Info,
				contentDescription = null,
				modifier = Modifier.size(20.dp),
				tint = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = "Your pantry is empty.",
					style = PurecipesTheme.typography.bodyMedium,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
				TextButton(
					onClick = onGoToPantry,
					modifier = Modifier.testTag(FILTER_KEY_INGREDIENTS_GO_TO_PANTRY_TAG),
				) {
					Text("Go to Pantry")
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun EmptyPantryCalloutPreview() {
	PurecipesTheme {
		EmptyPantryCallout(onGoToPantry = {})
	}
}

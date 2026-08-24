package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

internal const val COOKING_FLOATING_TIMER_TAG = "cookingFloatingTimer"

@Composable
internal fun CookingFloatingTimerChip(
	timer: CookingTimerState,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val containerColor = if (timer.isComplete) {
		PurecipesTheme.colorScheme.tertiaryContainer
	} else {
		PurecipesTheme.colorScheme.primaryContainer
	}
	val contentColor = if (timer.isComplete) {
		PurecipesTheme.colorScheme.onTertiaryContainer
	} else {
		PurecipesTheme.colorScheme.onPrimaryContainer
	}
	Surface(
		modifier = modifier.testTag(COOKING_FLOATING_TIMER_TAG),
		shape = RoundedCornerShape(PurecipesTheme.space.l),
		color = containerColor,
		shadowElevation = PurecipesTheme.space.xs,
	) {
		Row(
			modifier = Modifier.padding(
				start = PurecipesTheme.space.m,
				top = PurecipesTheme.space.s,
				end = PurecipesTheme.space.xs,
				bottom = PurecipesTheme.space.s,
			),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Icon(
				imageVector = if (timer.isComplete) Icons.Filled.Check else Icons.Filled.Timer,
				contentDescription = null,
				tint = contentColor,
				modifier = Modifier.size(TIMER_ICON_SIZE),
			)
			Text(
				text = if (timer.isComplete) {
					"Done · ${timer.label}"
				} else {
					"${timer.displayTime} · ${timer.label}"
				},
				style = PurecipesTheme.typography.titleMedium,
				color = contentColor,
			)
			IconButton(onClick = onDismiss) {
				Icon(
					imageVector = Icons.Filled.Close,
					contentDescription = "Dismiss timer",
					tint = contentColor,
				)
			}
		}
	}
}

private val TIMER_ICON_SIZE = 20.dp

@Preview(showBackground = true)
@Composable
private fun CookingFloatingTimerChipPreview() {
	PurecipesPreviewScaffold {
		CookingFloatingTimerChip(
			timer = CookingTimerState(
				label = "25 minutes",
				totalSeconds = 25 * 60,
				remainingSeconds = 12 * 60 + 34,
			),
			onDismiss = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun CookingFloatingTimerChipCompletePreview() {
	PurecipesPreviewScaffold {
		CookingFloatingTimerChip(
			timer = CookingTimerState(
				label = "5 min",
				totalSeconds = 5 * 60,
				remainingSeconds = 0,
			),
			onDismiss = {},
		)
	}
}

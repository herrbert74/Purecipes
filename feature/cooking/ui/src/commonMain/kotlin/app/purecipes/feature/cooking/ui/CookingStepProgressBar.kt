package app.purecipes.feature.cooking.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.cooking_step_progress_description

internal const val COOKING_STEP_PROGRESS_TAG = "cookingStepProgress"

private val COOKING_STEP_PROGRESS_HEIGHT = 10.dp
private const val COOKING_STEP_PROGRESS_DURATION_MS = 280

@Composable
internal fun CookingStepProgressBar(
	currentPageIndex: Int,
	stepCount: Int,
	modifier: Modifier = Modifier,
) {
	if (stepCount <= 0) return
	val currentStep = (currentPageIndex + 1).coerceIn(1, stepCount)
	val progressDescription = stringResource(
		Res.string.cooking_step_progress_description,
		currentStep,
		stepCount,
	)
	Row(
		modifier = modifier
			.fillMaxWidth()
			.testTag(COOKING_STEP_PROGRESS_TAG)
			.semantics {
				contentDescription = progressDescription
				progressBarRangeInfo = ProgressBarRangeInfo(
					current = currentStep.toFloat(),
					range = 1f..stepCount.toFloat(),
					steps = (stepCount - 1).coerceAtLeast(0),
				)
			},
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		repeat(stepCount) { index ->
			val filled = index <= currentPageIndex
			val targetColor = if (filled) {
				PurecipesTheme.colorScheme.primary
			} else {
				PurecipesTheme.colorScheme.surfaceContainerHighest
			}
			val color by animateColorAsState(
				targetValue = targetColor,
				animationSpec = tween(durationMillis = COOKING_STEP_PROGRESS_DURATION_MS),
				label = "cookingProgressSegment$index",
			)
			Box(
				modifier = Modifier
					.weight(1f)
					.height(COOKING_STEP_PROGRESS_HEIGHT)
					.clip(RoundedCornerShape(PurecipesTheme.space.xs))
					.background(color),
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun CookingStepProgressBarPreview() {
	PurecipesPreviewScaffold {
		CookingStepProgressBar(
			currentPageIndex = 1,
			stepCount = 4,
		)
	}
}

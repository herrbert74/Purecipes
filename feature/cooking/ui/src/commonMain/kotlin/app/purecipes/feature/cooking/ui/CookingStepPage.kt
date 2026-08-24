package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun CookingStepPage(
	stepNumber: Int,
	stepCount: Int,
	step: String,
	isLastStep: Boolean,
	onDurationClick: (CookingStepHighlight.Duration) -> Unit,
	onPrimaryAction: () -> Unit,
	modifier: Modifier = Modifier,
	timer: CookingTimerState? = null,
	onDismissTimer: () -> Unit = {},
) {
	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Text(
			text = "Step $stepNumber of $stepCount",
			style = PurecipesTheme.typography.titleMedium,
			color = PurecipesTheme.colorScheme.primary,
		)
		Column(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth()
				.verticalScroll(rememberScrollState()),
		) {
			CookingStepText(
				step = step,
				onDurationClick = onDurationClick,
			)
		}
		timer?.let { activeTimer ->
			CookingFloatingTimerChip(
				timer = activeTimer,
				onDismiss = onDismissTimer,
				modifier = Modifier.fillMaxWidth(),
			)
		}
		PurecipesButton(
			text = if (isLastStep) "Finish cooking" else "Next",
			onClick = onPrimaryAction,
			modifier = if (isLastStep) {
				Modifier.testTag(FINISH_COOKING_BUTTON_TAG)
			} else {
				Modifier
			},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun CookingStepPagePreview() {
	PurecipesPreviewScaffold {
		CookingStepPage(
			stepNumber = 2,
			stepCount = 4,
			step = "Simmer for 10 minutes at 180°C until thickened.",
			isLastStep = false,
			onDurationClick = {},
			onPrimaryAction = {},
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun CookingStepPageLastPreview() {
	PurecipesPreviewScaffold {
		CookingStepPage(
			stepNumber = 4,
			stepCount = 4,
			step = "Rest for 5 min, then serve.",
			isLastStep = true,
			onDurationClick = {},
			onPrimaryAction = {},
		)
	}
}

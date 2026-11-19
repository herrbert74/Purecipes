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
import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.ui.component.atom.PurecipesButton
import app.purecipes.shared.ui.component.atom.headingSemantics
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.cooking_step_header
import purecipes.shared.ui.generated.resources.finish_cooking
import purecipes.shared.ui.generated.resources.next_step

@Composable
internal fun CookingStepPage(
	stepNumber: Int,
	stepCount: Int,
	step: String,
	isLastStep: Boolean,
	onDurationClick: (CookingStepHighlight.Duration) -> Unit,
	onPrimaryAction: () -> Unit,
	modifier: Modifier = Modifier,
	appliances: ImmutableSet<Appliance> = persistentSetOf(),
	timer: CookingTimerState? = null,
	onDismissTimer: () -> Unit = {},
) {
	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
		) {
			Text(
				text = stringResource(Res.string.cooking_step_header, stepNumber, stepCount),
				modifier = Modifier.headingSemantics(),
				style = PurecipesTheme.typography.titleMedium,
				color = PurecipesTheme.colorScheme.primary,
			)
			if (appliances.isNotEmpty()) {
				CookingStepApplianceBadges(appliances = appliances)
			}
		}
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
			text = stringResource(
				if (isLastStep) Res.string.finish_cooking else Res.string.next_step,
			),
			onClick = onPrimaryAction,
			modifier = if (isLastStep) {
				Modifier.testTag(FINISH_COOKING_BUTTON_TAG)
			} else {
				Modifier
			},
		)
	}
}

private const val TIMER_CHIP_ENTER_SCALE = 0.92f

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

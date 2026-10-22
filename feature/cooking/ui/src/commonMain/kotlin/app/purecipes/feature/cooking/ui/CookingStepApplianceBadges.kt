package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.ui.component.ContainerTint
import app.purecipes.shared.ui.component.MetadataPillChip
import app.purecipes.shared.ui.icon.appliance.icon
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

const val COOKING_STEP_APPLIANCES_TAG = "cookingStepAppliances"
const val COOKING_STEP_APPLIANCE_CHIP_PREFIX = "cookingStepApplianceChip:"

@Composable
internal fun CookingStepApplianceBadges(
	appliances: ImmutableSet<Appliance>,
	modifier: Modifier = Modifier,
) {
	if (appliances.isEmpty()) return
	FlowRow(
		modifier = modifier.testTag(COOKING_STEP_APPLIANCES_TAG),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		appliances.forEach { appliance ->
			MetadataPillChip(
				text = appliance.displayName,
				modifier = Modifier.testTag("$COOKING_STEP_APPLIANCE_CHIP_PREFIX${appliance.name}"),
				icon = appliance.icon,
				tint = ContainerTint.Secondary,
				filled = false,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun CookingStepApplianceBadgesPreview() {
	PurecipesPreviewScaffold {
		CookingStepApplianceBadges(
			appliances = persistentSetOf(Appliance.OVEN, Appliance.AIR_FRYER),
		)
	}
}

package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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

const val RECIPE_APPLIANCES_ROW_TAG = "recipeAppliancesRow"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun RecipeAppliancesRow(
	appliances: ImmutableSet<Appliance>,
	modifier: Modifier = Modifier,
) {
	if (appliances.isEmpty()) return

	FlowRow(
		modifier = modifier
			.fillMaxWidth()
			.testTag(RECIPE_APPLIANCES_ROW_TAG),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		appliances.sortedBy { it.displayName }.forEach { appliance ->
			MetadataPillChip(
				text = appliance.displayName,
				icon = appliance.icon,
				tint = ContainerTint.Tertiary,
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun RecipeAppliancesRowPreview() {
	PurecipesPreviewScaffold {
		RecipeAppliancesRow(
			appliances = persistentSetOf(Appliance.OVEN, Appliance.AIR_FRYER, Appliance.BLENDER),
		)
	}
}

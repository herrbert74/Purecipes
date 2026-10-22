package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.ui.icon.appliance.icon
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

internal const val CREATE_RECIPE_APPLIANCES_CLEAR_ALL_TAG = "createRecipeAppliancesClearAll"
internal fun createRecipeApplianceItemTag(appliance: Appliance): String =
	"createRecipeApplianceItem_${appliance.name}"

@Composable
internal fun CreateRecipeAppliancesPickerSheet(
	selectedAppliances: ImmutableSet<Appliance>,
	onAppliancesChange: (Set<Appliance>) -> Unit,
	onDismiss: () -> Unit,
) {
	val applianceOptions = Appliance.entries
	val itemCount = applianceOptions.size
	val colors = createRecipeSegmentedListColors()

	ModalBottomSheet(onDismissRequest = onDismiss) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					start = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					top = PurecipesTheme.space.xs,
					bottom = PurecipesTheme.space.s,
				),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Text(
				text = "Required appliances",
				style = PurecipesTheme.typography.titleMedium,
			)
			if (selectedAppliances.isNotEmpty()) {
				TextButton(
					onClick = { onAppliancesChange(emptySet()) },
					modifier = Modifier.testTag(CREATE_RECIPE_APPLIANCES_CLEAR_ALL_TAG),
				) {
					Text(text = "Clear all")
				}
			}
		}
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = PurecipesTheme.space.m)
				.padding(bottom = PurecipesTheme.space.l),
			verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
		) {
			applianceOptions.forEachIndexed { index, appliance ->
				val isSelected = appliance in selectedAppliances
				SegmentedListItem(
					onClick = {
						val updated = if (isSelected) {
							selectedAppliances - appliance
						} else {
							selectedAppliances + appliance
						}
						onAppliancesChange(updated)
					},
					shapes = ListItemDefaults.segmentedShapes(index = index, count = itemCount),
					modifier = Modifier.testTag(createRecipeApplianceItemTag(appliance)),
					colors = colors,
					leadingContent = {
						Checkbox(
							checked = isSelected,
							onCheckedChange = null,
						)
					},
					trailingContent = {
						Icon(
							imageVector = appliance.icon,
							contentDescription = null,
							modifier = Modifier.size(24.dp),
							tint = if (isSelected) {
								PurecipesTheme.colorScheme.primary
							} else {
								PurecipesTheme.colorScheme.onSurfaceVariant
							},
						)
					},
					content = { Text(text = appliance.displayName) },
				)
			}
		}
	}
}

@Preview(
	name = "Create recipe appliances picker light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreateRecipeAppliancesPickerSheetPreview() {
	PurecipesTheme(darkTheme = false) {
		CreateRecipeAppliancesPickerSheet(
			selectedAppliances = persistentSetOf(Appliance.AIR_FRYER, Appliance.OVEN),
			onAppliancesChange = {},
			onDismiss = {},
		)
	}
}

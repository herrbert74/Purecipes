package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val CREATE_RECIPE_PRIVACY_SWITCH_TAG = "createRecipePrivacySwitch"
internal const val METADATA_GROUP_ITEM_COUNT = 4
internal const val PRIVACY_METADATA_INDEX = 3

@Composable
internal fun CreateRecipePrivacySection(
	isPrivate: Boolean,
	canMakePrivate: Boolean,
	onIsPrivateChange: (Boolean) -> Unit,
	onLockedClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	SegmentedListItem(
		onClick = {
			if (canMakePrivate) {
				onIsPrivateChange(!isPrivate)
			} else {
				onLockedClick()
			}
		},
		shapes = ListItemDefaults.segmentedShapes(
			index = PRIVACY_METADATA_INDEX,
			count = METADATA_GROUP_ITEM_COUNT,
		),
		modifier = modifier.testTag(CREATE_RECIPE_PRIVACY_SWITCH_TAG),
		colors = createRecipeSegmentedListColors(),
		trailingContent = {
			Row(verticalAlignment = Alignment.CenterVertically) {
				if (!canMakePrivate) {
					Icon(
						imageVector = Icons.Filled.Lock,
						contentDescription = "Private recipes are a premium feature",
					)
				}
				Switch(
					checked = isPrivate,
					onCheckedChange = if (canMakePrivate) {
						onIsPrivateChange
					} else {
						null
					},
					enabled = canMakePrivate,
				)
			}
		},
		supportingContent = {
			Text(
				text = if (canMakePrivate) {
					"Only you can find and open this recipe."
				} else {
					"Premium members can keep recipes visible only to themselves."
				},
			)
		},
		content = { Text(text = "Private recipe") },
	)
}

@Preview(
	name = "Create recipe privacy premium",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreateRecipePrivacySectionPremiumPreview() {
	PurecipesTheme(darkTheme = false) {
		CreateRecipePrivacySection(
			isPrivate = false,
			canMakePrivate = true,
			onIsPrivateChange = {},
			onLockedClick = {},
		)
	}
}

@Preview(
	name = "Create recipe privacy locked",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun CreateRecipePrivacySectionLockedPreview() {
	PurecipesTheme(darkTheme = false) {
		CreateRecipePrivacySection(
			isPrivate = false,
			canMakePrivate = false,
			onIsPrivateChange = {},
			onLockedClick = {},
		)
	}
}

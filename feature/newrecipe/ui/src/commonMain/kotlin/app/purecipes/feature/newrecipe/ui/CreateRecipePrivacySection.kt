package app.purecipes.feature.newrecipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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

@Composable
internal fun CreateRecipePrivacySection(
	isPrivate: Boolean,
	canMakePrivate: Boolean,
	onIsPrivateChange: (Boolean) -> Unit,
	onLockedClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.then(
					if (canMakePrivate) {
						Modifier
					} else {
						Modifier.clickable(onClick = onLockedClick)
					},
				)
				.padding(PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Column(
				modifier = Modifier
					.weight(1f)
					.padding(end = PurecipesTheme.space.m),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.quark),
			) {
				Text(
					text = "Private recipe",
					style = PurecipesTheme.typography.titleMedium,
				)
				Text(
					text = if (canMakePrivate) {
						"Only you can find and open this recipe."
					} else {
						"Premium members can keep recipes visible only to themselves."
					},
					style = PurecipesTheme.typography.bodySmall,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			if (!canMakePrivate) {
				Icon(
					imageVector = Icons.Filled.Lock,
					contentDescription = "Private recipes are a premium feature",
					tint = PurecipesTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.padding(end = PurecipesTheme.space.s),
				)
			}
			Switch(
				checked = isPrivate,
				onCheckedChange = onIsPrivateChange,
				enabled = canMakePrivate,
				modifier = Modifier.testTag(CREATE_RECIPE_PRIVACY_SWITCH_TAG),
			)
		}
	}
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
			modifier = Modifier.padding(PurecipesTheme.space.m),
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
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

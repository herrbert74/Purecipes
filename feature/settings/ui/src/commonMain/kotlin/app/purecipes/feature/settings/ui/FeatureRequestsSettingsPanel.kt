package app.purecipes.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val SETTINGS_FEATURE_REQUESTS_ROW_TAG = "settings_feature_requests_row"

@Composable
internal fun FeatureRequestsSettingsPanel(
	onOpenFeatureRequests: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			SectionHeader(
				title = "Feedback",
				subtitle = "Vote on ideas and tell us what to build next.",
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onOpenFeatureRequests)
					.padding(vertical = PurecipesTheme.space.xs)
					.testTag(SETTINGS_FEATURE_REQUESTS_ROW_TAG),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = "Feature requests",
					style = PurecipesTheme.typography.bodyMedium,
					fontWeight = FontWeight.Medium,
					modifier = Modifier.weight(1f),
				)
				Icon(
					imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
					contentDescription = null,
					tint = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Preview(
	name = "Feature requests settings light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun FeatureRequestsSettingsPanelLightPreview() {
	PurecipesTheme(darkTheme = false) {
		FeatureRequestsSettingsPanel(
			onOpenFeatureRequests = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Feature requests settings dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun FeatureRequestsSettingsPanelDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		FeatureRequestsSettingsPanel(
			onOpenFeatureRequests = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

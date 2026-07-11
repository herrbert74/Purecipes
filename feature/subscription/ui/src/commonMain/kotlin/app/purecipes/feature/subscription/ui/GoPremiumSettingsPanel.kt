package app.purecipes.feature.subscription.ui

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
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val GO_PREMIUM_SETTINGS_ROW_TAG = "goPremiumSettingsRow"

@Composable
fun GoPremiumSettingsPanel(
	onOpenPaywall: () -> Unit,
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
				title = "Go Premium",
				subtitle = "Cook ad-free and unlock key ingredients plus nutrition filters.",
			)
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = onOpenPaywall)
					.padding(vertical = PurecipesTheme.space.xs)
					.testTag(GO_PREMIUM_SETTINGS_ROW_TAG),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween,
			) {
				Text(
					text = "View plans",
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

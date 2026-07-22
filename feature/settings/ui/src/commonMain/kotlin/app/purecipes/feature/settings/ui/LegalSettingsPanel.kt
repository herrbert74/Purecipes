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
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val SETTINGS_PRIVACY_POLICY_ROW_TAG = "settings_privacy_policy_row"
internal const val SETTINGS_TERMS_OF_SERVICE_ROW_TAG = "settings_terms_of_service_row"

@Composable
internal fun LegalSettingsPanel(
	onOpenPrivacyPolicy: () -> Unit,
	onOpenTermsOfService: () -> Unit,
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
				title = "Legal",
				subtitle = "Terms and privacy information.",
			)
			LegalSettingsRow(
				label = "Privacy Policy",
				onClick = onOpenPrivacyPolicy,
				testTag = SETTINGS_PRIVACY_POLICY_ROW_TAG,
			)
			LegalSettingsRow(
				label = "Terms of Service",
				onClick = onOpenTermsOfService,
				testTag = SETTINGS_TERMS_OF_SERVICE_ROW_TAG,
			)
		}
	}
}

@Composable
private fun LegalSettingsRow(
	label: String,
	onClick: () -> Unit,
	testTag: String,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(vertical = PurecipesTheme.space.xs)
			.testTag(testTag),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.SpaceBetween,
	) {
		Text(
			text = label,
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

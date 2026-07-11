package app.purecipes.feature.subscription.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val MONETISATION_DEBUG_OVERRIDES_PANEL_TAG = "monetisationDebugOverridesPanel"
internal const val MONETISATION_DEBUG_PREMIUM_AUTO_TAG = "monetisationDebugPremiumAuto"
internal const val MONETISATION_DEBUG_PREMIUM_FREE_TAG = "monetisationDebugPremiumFree"
internal const val MONETISATION_DEBUG_PREMIUM_PREMIUM_TAG = "monetisationDebugPremiumPremium"
internal const val MONETISATION_DEBUG_ADS_AUTO_TAG = "monetisationDebugAdsAuto"
internal const val MONETISATION_DEBUG_ADS_ON_TAG = "monetisationDebugAdsOn"
internal const val MONETISATION_DEBUG_ADS_OFF_TAG = "monetisationDebugAdsOff"

@Composable
fun MonetisationDebugOverridesPanel(
	overrides: MonetisationDebugOverrides,
	onPremiumStatusChange: (PremiumStatusOverride) -> Unit,
	onAdsDisplayChange: (AdsDisplayOverride) -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.testTag(MONETISATION_DEBUG_OVERRIDES_PANEL_TAG),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Column(
			modifier = Modifier.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			SectionHeader(
				title = "Monetisation debug",
				subtitle = "Override subscription and ads behaviour on this device.",
			)
			Text(
				text = "Subscription",
				style = PurecipesTheme.typography.labelLarge,
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				FilterChip(
					selected = overrides.premiumStatus == PremiumStatusOverride.AUTO,
					onClick = { onPremiumStatusChange(PremiumStatusOverride.AUTO) },
					label = { Text("Auto") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_PREMIUM_AUTO_TAG),
				)
				FilterChip(
					selected = overrides.premiumStatus == PremiumStatusOverride.FORCE_FREE,
					onClick = { onPremiumStatusChange(PremiumStatusOverride.FORCE_FREE) },
					label = { Text("Free") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_PREMIUM_FREE_TAG),
				)
				FilterChip(
					selected = overrides.premiumStatus == PremiumStatusOverride.FORCE_PREMIUM,
					onClick = { onPremiumStatusChange(PremiumStatusOverride.FORCE_PREMIUM) },
					label = { Text("Premium") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_PREMIUM_PREMIUM_TAG),
				)
			}
			Text(
				text = "Ads",
				style = PurecipesTheme.typography.labelLarge,
			)
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
			) {
				FilterChip(
					selected = overrides.adsDisplay == AdsDisplayOverride.AUTO,
					onClick = { onAdsDisplayChange(AdsDisplayOverride.AUTO) },
					label = { Text("Auto") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_ADS_AUTO_TAG),
				)
				FilterChip(
					selected = overrides.adsDisplay == AdsDisplayOverride.FORCE_ON,
					onClick = { onAdsDisplayChange(AdsDisplayOverride.FORCE_ON) },
					label = { Text("On") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_ADS_ON_TAG),
				)
				FilterChip(
					selected = overrides.adsDisplay == AdsDisplayOverride.FORCE_OFF,
					onClick = { onAdsDisplayChange(AdsDisplayOverride.FORCE_OFF) },
					label = { Text("Off") },
					modifier = Modifier.testTag(MONETISATION_DEBUG_ADS_OFF_TAG),
				)
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun MonetisationDebugOverridesPanelPreview() {
	PurecipesTheme {
		MonetisationDebugOverridesPanel(
			overrides = MonetisationDebugOverrides(),
			onPremiumStatusChange = {},
			onAdsDisplayChange = {},
		)
	}
}

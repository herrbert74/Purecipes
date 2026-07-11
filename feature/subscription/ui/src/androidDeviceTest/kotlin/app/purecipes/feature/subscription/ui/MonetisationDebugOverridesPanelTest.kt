package app.purecipes.feature.subscription.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.subscription.domain.model.AdsDisplayOverride
import app.purecipes.feature.subscription.domain.model.MonetisationDebugOverrides
import app.purecipes.feature.subscription.domain.model.PremiumStatusOverride
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class MonetisationDebugOverridesPanelTest {

	@Test
	fun selectingPremiumAndAdsOverridesInvokesCallbacks() = runRecompositionTrackingUiTest {
		var premiumOverride by mutableStateOf(PremiumStatusOverride.AUTO)
		var adsOverride by mutableStateOf(AdsDisplayOverride.AUTO)
		setTrackedContent {
			PurecipesTheme {
				MonetisationDebugOverridesPanel(
					overrides = MonetisationDebugOverrides(
						premiumStatus = premiumOverride,
						adsDisplay = adsOverride,
					),
					onPremiumStatusChange = { premiumOverride = it },
					onAdsDisplayChange = { adsOverride = it },
				)
			}
		}

		onNodeWithTag(MONETISATION_DEBUG_OVERRIDES_PANEL_TAG).assertIsDisplayed()
		onNodeWithTag(MONETISATION_DEBUG_PREMIUM_AUTO_TAG).assertIsSelected()
		onNodeWithTag(MONETISATION_DEBUG_PREMIUM_PREMIUM_TAG).performClick()
		onNodeWithTag(MONETISATION_DEBUG_ADS_OFF_TAG).performClick()

		assertEquals(PremiumStatusOverride.FORCE_PREMIUM, premiumOverride)
		assertEquals(AdsDisplayOverride.FORCE_OFF, adsOverride)
	}
}

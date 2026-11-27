package app.purecipes.feature.settings.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.testfixtures.uitest.runRecompositionTrackingUiTest
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.setTrackedContent
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class LegalSettingsPanelTest {

	@Test
	fun legalSettingsPanelInvokesLinkCallbacks() = runRecompositionTrackingUiTest {
		var openedPrivacyPolicy = false
		var openedTermsOfService = false
		setTrackedContent {
			PurecipesTheme {
				LegalSettingsPanel(
					onOpenPrivacyPolicy = { openedPrivacyPolicy = true },
					onOpenTermsOfService = { openedTermsOfService = true },
				)
			}
		}

		onNodeWithTag(SETTINGS_PRIVACY_POLICY_ROW_TAG).performClick()
		onNodeWithTag(SETTINGS_TERMS_OF_SERVICE_ROW_TAG).performClick()

		openedPrivacyPolicy.shouldBeTrue()
		openedTermsOfService.shouldBeTrue()
	}
}

package app.purecipes.feature.settings.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class AboutSettingsPanelTest {

	@Test
	fun aboutSettingsPanelInvokesOpenAboutCallback() = runRecompositionTrackingUiTest {
		var openedAbout = false
		setTrackedContent {
			PurecipesTheme {
				AboutSettingsPanel(onOpenAbout = { openedAbout = true })
			}
		}

		onNodeWithTag(SETTINGS_ABOUT_ROW_TAG).performClick()

		assertTrue(openedAbout)
	}
}

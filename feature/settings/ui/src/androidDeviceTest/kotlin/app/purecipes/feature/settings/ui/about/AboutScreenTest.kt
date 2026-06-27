package app.purecipes.feature.settings.ui.about

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class AboutScreenTest {

	@Test
	fun aboutScreenShowsVersionText() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				AboutScreen(
					onBack = {},
					viewModel = AboutViewModel(
						purecipesConfig = fakePurecipesConfig(
							versionName = "1.2.3",
							versionCode = 42L,
						),
					),
				)
			}
		}

		onNodeWithTag(ABOUT_VERSION_ROW_TAG).assertIsDisplayed()
		onNodeWithText("Version 1.2.3 (42)").assertIsDisplayed()
	}

	@Test
	fun placeholderRowShowsComingSoonSnackbar() = runRecompositionTrackingUiTest {
		setTrackedContent {
			PurecipesTheme {
				AboutScreen(
					onBack = {},
					viewModel = AboutViewModel(purecipesConfig = fakePurecipesConfig()),
				)
			}
		}

		onNodeWithTag(ABOUT_TERMS_ROW_TAG).performClick()
		waitForIdle()

		onNodeWithText("Coming soon").assertIsDisplayed()
	}

	private fun fakePurecipesConfig(
		versionName: String = "0.0.0",
		versionCode: Long = 0L,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = versionName

		override fun versionCode(): Long = versionCode
	}
}

package app.purecipes.feature.settings.ui.about

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.onboarding.domain.usecase.ResetOnboardingUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeOnboardingRepository
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
					onOpenLicenses = {},
					viewModel = aboutViewModel(versionName = "1.2.3", versionCode = 42L),
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
					onOpenLicenses = {},
					viewModel = aboutViewModel(),
				)
			}
		}

		onNodeWithTag(ABOUT_TERMS_ROW_TAG).performClick()
		waitForIdle()

		onNodeWithText("Coming soon").assertIsDisplayed()
	}

	@Test
	fun openSourceLicensesRowInvokesOpenLicensesCallback() = runRecompositionTrackingUiTest {
		var openedLicenses = false
		setTrackedContent {
			PurecipesTheme {
				AboutScreenContent(
					versionText = "Version 1.2.3 (42)",
					onPlaceholderClick = {},
					onVersionClick = {},
					onOpenLicenses = { openedLicenses = true },
				)
			}
		}

		onNodeWithTag(ABOUT_OSS_ROW_TAG).performClick()

		assertTrue(openedLicenses)
	}

	@Test
	fun tappingTheVersionSevenTimesResetsOnboarding() = runRecompositionTrackingUiTest {
		val onboardingRepository = FakeOnboardingRepository(completed = true)
		setTrackedContent {
			PurecipesTheme {
				AboutScreen(
					onBack = {},
					onOpenLicenses = {},
					viewModel = aboutViewModel(onboardingRepository = onboardingRepository),
				)
			}
		}

		repeat(ONBOARDING_RESET_TAP_COUNT - 1) {
			onNodeWithTag(ABOUT_VERSION_ROW_TAG).performClick()
		}
		waitForIdle()
		assertEquals(0, onboardingRepository.resetOnboardingCallCount)

		onNodeWithTag(ABOUT_VERSION_ROW_TAG).performClick()
		waitForIdle()

		assertEquals(1, onboardingRepository.resetOnboardingCallCount)
		onNodeWithText("Onboarding will show the next time you open the app").assertIsDisplayed()
	}

	private fun aboutViewModel(
		versionName: String = "0.0.0",
		versionCode: Long = 0L,
		onboardingRepository: FakeOnboardingRepository = FakeOnboardingRepository(),
	): AboutViewModel = AboutViewModel(
		purecipesConfig = fakePurecipesConfig(
			versionName = versionName,
			versionCode = versionCode,
		),
		resetOnboarding = ResetOnboardingUseCase(onboardingRepository),
		trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	)

	private fun fakePurecipesConfig(
		versionName: String = "0.0.0",
		versionCode: Long = 0L,
	): PurecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

		override fun versionName(): String = versionName

		override fun versionCode(): Long = versionCode
	}
}

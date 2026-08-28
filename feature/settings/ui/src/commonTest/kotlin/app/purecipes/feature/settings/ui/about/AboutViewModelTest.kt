package app.purecipes.feature.settings.ui.about

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.onboarding.domain.usecase.ResetOnboardingUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeOnboardingRepository
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AboutViewModelTest {

	@Test
	fun `version text includes version name and code`() = runViewModelTest {
		val viewModel = aboutViewModel(versionName = "1.2.3", versionCode = 42L)

		viewModel.versionText shouldBe "Version 1.2.3 (42)"
	}

	@Test
	fun `tapping the version fewer times than needed leaves onboarding completed`() =
		runUnconfinedViewModelTest {
			val onboardingRepository = FakeOnboardingRepository(completed = true)
			val viewModel = aboutViewModel(onboardingRepository = onboardingRepository)

			repeat(ONBOARDING_RESET_TAP_COUNT - 1) {
				viewModel.onVersionTapped() shouldBe false
			}

			onboardingRepository.resetOnboardingCallCount shouldBe 0
			onboardingRepository.isOnboardingCompleted() shouldBe true
		}

	@Test
	fun `tapping the version enough times resets onboarding`() = runUnconfinedViewModelTest {
		val onboardingRepository = FakeOnboardingRepository(completed = true)
		val viewModel = aboutViewModel(onboardingRepository = onboardingRepository)

		repeat(ONBOARDING_RESET_TAP_COUNT - 1) { viewModel.onVersionTapped() }

		viewModel.onVersionTapped() shouldBe true
		onboardingRepository.resetOnboardingCallCount shouldBe 1
		onboardingRepository.isOnboardingCompleted() shouldBe false
	}

	@Test
	fun `tapping the version enough times reports a reset to analytics`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = aboutViewModel(analyticsRepository = analyticsRepository)

		repeat(ONBOARDING_RESET_TAP_COUNT) { viewModel.onVersionTapped() }

		analyticsRepository.trackedEvents shouldBe listOf(AnalyticsEvent.OnboardingReset)
	}

	@Test
	fun `the tap counter starts over after a reset`() = runUnconfinedViewModelTest {
		val onboardingRepository = FakeOnboardingRepository(completed = true)
		val viewModel = aboutViewModel(onboardingRepository = onboardingRepository)

		repeat(ONBOARDING_RESET_TAP_COUNT) { viewModel.onVersionTapped() }
		viewModel.onVersionTapped() shouldBe false

		repeat(ONBOARDING_RESET_TAP_COUNT - 1) { viewModel.onVersionTapped() }

		onboardingRepository.resetOnboardingCallCount shouldBe 2
	}

	private fun aboutViewModel(
		versionName: String = "0.0.0",
		versionCode: Long = 0L,
		onboardingRepository: FakeOnboardingRepository = FakeOnboardingRepository(),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	): AboutViewModel = AboutViewModel(
		purecipesConfig = fakePurecipesConfig(
			versionName = versionName,
			versionCode = versionCode,
		),
		resetOnboarding = ResetOnboardingUseCase(onboardingRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
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

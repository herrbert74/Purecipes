package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.onboarding.ui.OnboardingOutcome
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeOnboardingRepository
import app.purecipes.shared.testfixtures.runUnconfinedViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelOnboardingTest {

	@Test
	fun `onboarding is visible when it has never been completed`() = runUnconfinedViewModelTest {
		val viewModel = mainViewModelForTest(
			onboardingRepository = FakeOnboardingRepository(completed = false),
		)

		viewModel.onboardingGate.isVisible shouldBe true
	}

	@Test
	fun `onboarding stays hidden once completed`() = runUnconfinedViewModelTest {
		val viewModel = mainViewModelForTest(
			onboardingRepository = FakeOnboardingRepository(completed = true),
		)

		viewModel.onboardingGate.isVisible shouldBe false
	}

	@Test
	fun `finishing onboarding hides it and persists completion`() = runUnconfinedViewModelTest {
		val onboardingRepository = FakeOnboardingRepository(completed = false)
		val viewModel = mainViewModelForTest(onboardingRepository = onboardingRepository)

		viewModel.onboardingGate.onFinished(
			OnboardingOutcome(skipped = false, lastPageIndex = 3, pageCount = 4),
		)

		viewModel.onboardingGate.isVisible shouldBe false
		onboardingRepository.isOnboardingCompleted() shouldBe true
		onboardingRepository.completeOnboardingCallCount shouldBe 1
	}

	@Test
	fun `skipping onboarding reports the skip to analytics`() = runUnconfinedViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = mainViewModelForTest(
			analyticsRepository = analyticsRepository,
			onboardingRepository = FakeOnboardingRepository(completed = false),
		)

		viewModel.onboardingGate.onFinished(
			OnboardingOutcome(skipped = true, lastPageIndex = 1, pageCount = 4),
		)

		analyticsRepository.trackedEvents
			.filterIsInstance<AnalyticsEvent.OnboardingCompleted>() shouldBe listOf(
			AnalyticsEvent.OnboardingCompleted(skipped = true, lastPageIndex = 1, pageCount = 4),
		)
	}

	@Test
	fun `finishing onboarding twice only persists once`() = runUnconfinedViewModelTest {
		val onboardingRepository = FakeOnboardingRepository(completed = false)
		val viewModel = mainViewModelForTest(onboardingRepository = onboardingRepository)
		val outcome = OnboardingOutcome(skipped = false, lastPageIndex = 3, pageCount = 4)

		viewModel.onboardingGate.onFinished(outcome)
		viewModel.onboardingGate.onFinished(outcome)

		onboardingRepository.completeOnboardingCallCount shouldBe 1
	}
}

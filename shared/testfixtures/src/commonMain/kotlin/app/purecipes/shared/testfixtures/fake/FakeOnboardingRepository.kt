package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.onboarding.domain.repository.OnboardingRepository

class FakeOnboardingRepository(
	completed: Boolean = false,
) : OnboardingRepository {

	private var completed = completed

	var completeOnboardingCallCount = 0
		private set

	var resetOnboardingCallCount = 0
		private set

	override fun isOnboardingCompleted(): Boolean = completed

	override suspend fun completeOnboarding() {
		completed = true
		completeOnboardingCallCount++
	}

	override suspend fun resetOnboarding() {
		completed = false
		resetOnboardingCallCount++
	}
}

package app.purecipes.feature.onboarding.domain.usecase

import app.purecipes.feature.onboarding.domain.repository.OnboardingRepository
import dev.zacsweers.metro.Inject

@Inject
class ResetOnboardingUseCase(
	private val repository: OnboardingRepository,
) {

	suspend operator fun invoke() {
		repository.resetOnboarding()
	}
}

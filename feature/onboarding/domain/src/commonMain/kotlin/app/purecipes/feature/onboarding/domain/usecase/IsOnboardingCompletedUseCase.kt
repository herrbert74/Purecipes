package app.purecipes.feature.onboarding.domain.usecase

import app.purecipes.feature.onboarding.domain.repository.OnboardingRepository
import dev.zacsweers.metro.Inject

@Inject
class IsOnboardingCompletedUseCase(
	private val repository: OnboardingRepository,
) {

	operator fun invoke(): Boolean = repository.isOnboardingCompleted()
}

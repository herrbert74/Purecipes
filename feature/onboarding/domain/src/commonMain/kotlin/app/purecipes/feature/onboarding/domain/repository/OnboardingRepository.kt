package app.purecipes.feature.onboarding.domain.repository

interface OnboardingRepository {

	fun isOnboardingCompleted(): Boolean

	suspend fun completeOnboarding()

	suspend fun resetOnboarding()
}

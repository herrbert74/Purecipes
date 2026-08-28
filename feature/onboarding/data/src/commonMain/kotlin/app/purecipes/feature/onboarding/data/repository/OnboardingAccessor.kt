package app.purecipes.feature.onboarding.data.repository

import app.purecipes.feature.onboarding.data.datasource.OnboardingDataSource
import app.purecipes.feature.onboarding.domain.repository.OnboardingRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class OnboardingAccessor(
	private val localDataSource: OnboardingDataSource.Local,
) : OnboardingRepository {

	override fun isOnboardingCompleted(): Boolean = localDataSource.isOnboardingCompleted()

	override suspend fun completeOnboarding() {
		localDataSource.setOnboardingCompleted(completed = true)
	}

	override suspend fun resetOnboarding() {
		localDataSource.setOnboardingCompleted(completed = false)
	}
}

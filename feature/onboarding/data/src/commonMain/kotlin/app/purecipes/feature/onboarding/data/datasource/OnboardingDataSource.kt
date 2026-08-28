package app.purecipes.feature.onboarding.data.datasource

interface OnboardingDataSource {

	interface Local {

		fun isOnboardingCompleted(): Boolean

		fun setOnboardingCompleted(completed: Boolean)
	}
}

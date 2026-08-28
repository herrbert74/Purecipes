package app.purecipes.feature.onboarding.ui

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingOutcome(
	val skipped: Boolean,
	val lastPageIndex: Int,
	val pageCount: Int,
)

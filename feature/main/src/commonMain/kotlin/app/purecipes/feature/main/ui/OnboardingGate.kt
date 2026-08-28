package app.purecipes.feature.main.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import app.purecipes.feature.onboarding.domain.usecase.IsOnboardingCompletedUseCase
import app.purecipes.feature.onboarding.ui.OnboardingOutcome
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * First launch gate for the onboarding walkthrough.
 *
 * The completion flag is read eagerly from local storage so the first frame after the splash
 * already knows whether onboarding is due and the main UI never flashes behind the walkthrough.
 */
internal class OnboardingGate(
	isOnboardingCompleted: IsOnboardingCompletedUseCase,
	private val completeOnboarding: CompleteOnboardingUseCase,
	private val trackEvent: TrackEventUseCase,
	private val scope: CoroutineScope,
) {

	var isVisible by mutableStateOf(!isOnboardingCompleted())
		private set

	fun onFinished(outcome: OnboardingOutcome) {
		if (!isVisible) {
			return
		}
		isVisible = false
		trackEvent(
			AnalyticsEvent.OnboardingCompleted(
				skipped = outcome.skipped,
				lastPageIndex = outcome.lastPageIndex,
				pageCount = outcome.pageCount,
			),
		)
		scope.launch {
			completeOnboarding()
		}
	}
}

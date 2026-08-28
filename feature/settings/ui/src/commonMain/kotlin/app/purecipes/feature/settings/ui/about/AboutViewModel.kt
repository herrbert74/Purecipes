package app.purecipes.feature.settings.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.onboarding.domain.usecase.ResetOnboardingUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.launch

internal const val ONBOARDING_RESET_TAP_COUNT = 7

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class AboutViewModel(
	purecipesConfig: PurecipesConfig,
	private val resetOnboarding: ResetOnboardingUseCase,
	private val trackEvent: TrackEventUseCase,
) : ViewModel() {

	val versionText: String =
		"Version ${purecipesConfig.versionName()} (${purecipesConfig.versionCode()})"

	private var versionTapCount = 0

	/**
	 * Hidden shortcut for testing the walkthrough on a store build: tapping the version
	 * [ONBOARDING_RESET_TAP_COUNT] times clears the completion flag, so the next cold start shows
	 * onboarding again. The counter dies with the screen, so stray taps never accumulate.
	 *
	 * Returns whether this tap triggered the reset, so the caller can confirm it.
	 */
	fun onVersionTapped(): Boolean {
		versionTapCount++
		if (versionTapCount < ONBOARDING_RESET_TAP_COUNT) {
			return false
		}
		versionTapCount = 0
		trackEvent(AnalyticsEvent.OnboardingReset)
		viewModelScope.launch {
			resetOnboarding()
		}
		return true
	}
}

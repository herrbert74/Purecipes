package app.purecipes.feature.analytics.ui

import androidx.lifecycle.ViewModel
import app.purecipes.feature.analytics.domain.model.AnalyticsScreenName
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackScreenViewUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.StateFlow

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class ConsentViewModel(
	observeConsentState: ObserveConsentStateUseCase,
	private val showConsentForm: ShowConsentFormUseCase,
	trackScreenView: TrackScreenViewUseCase,
) : ViewModel() {

	val consentState: StateFlow<ConsentState> = observeConsentState()

	init {
		trackScreenView(AnalyticsScreenName.CONSENT_PREFERENCES)
	}

	fun onManagePrivacySettingsClick() {
		showConsentForm()
	}
}

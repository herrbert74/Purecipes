package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.ConsentState
import kotlinx.coroutines.flow.StateFlow

interface ConsentDataSource {
	val consentState: StateFlow<ConsentState>

	fun refreshConsent()

	fun showConsentForm()
}

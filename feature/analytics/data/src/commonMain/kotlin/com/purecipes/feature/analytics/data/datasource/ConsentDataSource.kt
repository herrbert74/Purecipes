package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.ConsentState
import kotlinx.coroutines.flow.StateFlow

internal interface ConsentDataSource {
	val consentState: StateFlow<ConsentState>

	fun refreshConsent()

	fun showConsentForm()
}

package com.purecipes.feature.analytics.domain.repository

import com.purecipes.feature.analytics.domain.model.ConsentState
import kotlinx.coroutines.flow.StateFlow

interface ConsentRepository {
	fun observeConsentState(): StateFlow<ConsentState>

	fun currentConsentState(): ConsentState

	fun refreshConsent()

	fun showConsentForm()
}
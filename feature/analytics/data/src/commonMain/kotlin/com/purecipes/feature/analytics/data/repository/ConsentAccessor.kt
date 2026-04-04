package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.ConsentDataSource
import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.feature.analytics.domain.repository.ConsentRepository
import kotlinx.coroutines.flow.StateFlow

internal class ConsentAccessor(
	private val consentDataSource: ConsentDataSource,
) : ConsentRepository {

	override fun observeConsentState(): StateFlow<ConsentState> = consentDataSource.consentState

	override fun currentConsentState(): ConsentState = consentDataSource.consentState.value

	override fun refreshConsent() {
		consentDataSource.refreshConsent()
	}

	override fun showConsentForm() {
		consentDataSource.showConsentForm()
	}
}

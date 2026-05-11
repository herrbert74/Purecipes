package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeConsentRepository(initialState: ConsentState) : ConsentRepository {

	private val mutableConsentState = MutableStateFlow(initialState)
	var refreshConsentCalled = false
	var showConsentFormCalled = false

	override fun observeConsentState(): StateFlow<ConsentState> = mutableConsentState

	override fun currentConsentState(): ConsentState = mutableConsentState.value

	override fun refreshConsent() {
		refreshConsentCalled = true
	}

	override fun showConsentForm() {
		showConsentFormCalled = true
	}

	fun updateConsentState(consentState: ConsentState) {
		mutableConsentState.value = consentState
	}
}

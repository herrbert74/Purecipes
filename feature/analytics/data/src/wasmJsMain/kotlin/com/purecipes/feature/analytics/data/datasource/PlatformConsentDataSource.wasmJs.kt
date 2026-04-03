package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.shared.data.config.PurecipesConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal actual class PlatformConsentDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {

	private val mutableConsentState = MutableStateFlow(
		if (purecipesConfig.usercentricsSettingsId().isNullOrBlank()) {
			ConsentState.NOT_REQUIRED
		} else {
			ConsentState.UNKNOWN
		},
	)

	actual override val consentState: StateFlow<ConsentState> = mutableConsentState

	actual override fun refreshConsent() {
		if (mutableConsentState.value == ConsentState.UNKNOWN) {
			mutableConsentState.value = ConsentState.REQUIRED
		}
	}

	actual override fun showConsentForm() {
		if (mutableConsentState.value != ConsentState.NOT_REQUIRED) {
			mutableConsentState.value = ConsentState.OBTAINED
		}
	}
}
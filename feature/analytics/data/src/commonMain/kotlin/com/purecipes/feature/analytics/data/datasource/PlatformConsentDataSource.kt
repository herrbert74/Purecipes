package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.ConsentState
import com.purecipes.shared.data.config.PurecipesConfig
import kotlinx.coroutines.flow.StateFlow

internal expect class PlatformConsentDataSource(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {
	override val consentState: StateFlow<ConsentState>

	override fun refreshConsent()

	override fun showConsentForm()
}
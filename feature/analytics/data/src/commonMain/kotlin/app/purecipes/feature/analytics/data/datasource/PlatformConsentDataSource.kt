package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.shared.data.config.PurecipesConfig
import kotlinx.coroutines.flow.StateFlow

internal expect class PlatformConsentDataSource(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {
	override val consentState: StateFlow<ConsentState>

	override fun refreshConsent()

	override fun showConsentForm()
}

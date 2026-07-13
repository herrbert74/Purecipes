package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.runtime.ConsentBridgeState
import app.purecipes.feature.analytics.domain.runtime.IosAnalyticsNativeBridge
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
actual class PlatformConsentDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {

	private val settingsId = purecipesConfig.usercentricsSettingsId().orEmpty()
	private val mutableConsentState = MutableStateFlow(
		if (settingsId.isBlank()) {
			ConsentState.NOT_REQUIRED
		} else {
			ConsentState.UNKNOWN
		},
	)

	init {
		if (settingsId.isNotBlank()) {
			IosAnalyticsNativeBridge.startObservingConsent(settingsId) { bridgeState ->
				mutableConsentState.value = bridgeState.toConsentState()
			}
		}
	}

	actual override val consentState: StateFlow<ConsentState> = mutableConsentState

	actual override fun refreshConsent() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		IosAnalyticsNativeBridge.refreshConsent(settingsId) { bridgeState ->
			mutableConsentState.value = bridgeState.toConsentState()
		}
	}

	actual override fun showConsentForm() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		IosAnalyticsNativeBridge.showConsentForm()
	}
}

private fun String.toConsentState(): ConsentState {
	return when (runCatching { ConsentBridgeState.valueOf(this) }.getOrNull()) {
		ConsentBridgeState.NOT_REQUIRED -> ConsentState.NOT_REQUIRED
		ConsentBridgeState.REQUIRED -> ConsentState.REQUIRED
		ConsentBridgeState.OBTAINED -> ConsentState.OBTAINED
		ConsentBridgeState.DENIED -> ConsentState.DENIED
		ConsentBridgeState.UNKNOWN, null -> ConsentState.UNKNOWN
	}
}

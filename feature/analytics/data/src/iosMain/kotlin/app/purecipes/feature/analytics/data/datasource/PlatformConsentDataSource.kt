package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.runtime.IosAnalyticsNativeBridge
import app.purecipes.shared.data.config.PurecipesConfig
import cocoapods.Usercentrics.UsercentricsUpdatedConsentPayload
import cocoapods.Usercentrics.UsercentricsUsercentricsEvent
import cocoapods.Usercentrics.UsercentricsUsercentricsKt
import cocoapods.Usercentrics.UsercentricsUsercentricsLoggerLevel
import cocoapods.Usercentrics.UsercentricsUsercentricsOptions
import cocoapods.Usercentrics.UsercentricsUsercentricsReadyStatus
import cocoapods.Usercentrics.UsercentricsUsercentricsServiceConsent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal actual class PlatformConsentDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : ConsentDataSource {

	private val settingsId = purecipesConfig.usercentricsSettingsId().orEmpty()
	private var isConfigured = false
	private var consentUpdatedSubscription: Any? = null
	private val mutableConsentState = MutableStateFlow(
		if (settingsId.isBlank()) {
			ConsentState.NOT_REQUIRED
		} else {
			ConsentState.UNKNOWN
		},
	)

	actual override val consentState: StateFlow<ConsentState> = mutableConsentState

	actual override fun refreshConsent() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		configureIfNeeded()
		UsercentricsUsercentricsKt.isReadyOnSuccess(
			onSuccess = { status ->
				mutableConsentState.value = status.toConsentState()
			},
			onFailure = {
				mutableConsentState.value = ConsentState.UNKNOWN
			},
		)
	}

	actual override fun showConsentForm() {
		if (settingsId.isBlank()) {
			mutableConsentState.value = ConsentState.NOT_REQUIRED
			return
		}
		configureIfNeeded()
		IosAnalyticsNativeBridge.showConsentForm()
	}

	private fun configureIfNeeded() {
		if (isConfigured) {
			return
		}
		val options = UsercentricsUsercentricsOptions(settingsId = settingsId)
		options.loggerLevel = UsercentricsUsercentricsLoggerLevel.none
		UsercentricsUsercentricsKt.configureOptions(options)
		consentUpdatedSubscription = UsercentricsUsercentricsEvent.shared.onConsentUpdatedCallback(
			callback = { payload ->
				mutableConsentState.value = payload.toConsentState()
			},
		)
		isConfigured = true
	}
}

private fun UsercentricsUsercentricsReadyStatus?.toConsentState(): ConsentState {
	if (this == null) {
		return ConsentState.UNKNOWN
	}
	if (shouldCollectConsent) {
		return ConsentState.REQUIRED
	}
	return consentStateFrom(consents)
}

private fun UsercentricsUpdatedConsentPayload?.toConsentState(): ConsentState {
	if (this == null) {
		return ConsentState.UNKNOWN
	}
	return consentStateFrom(consents)
}

private fun consentStateFrom(consents: Any?): ConsentState {
	val serviceConsents = (consents as? List<*>)
		.orEmpty()
		.filterIsInstance<UsercentricsUsercentricsServiceConsent>()
	val nonEssentialConsents = serviceConsents.filterNot { it.isEssential }
	return when {
		nonEssentialConsents.isEmpty() -> ConsentState.OBTAINED
		nonEssentialConsents.any { it.status } -> ConsentState.OBTAINED
		else -> ConsentState.DENIED
	}
}

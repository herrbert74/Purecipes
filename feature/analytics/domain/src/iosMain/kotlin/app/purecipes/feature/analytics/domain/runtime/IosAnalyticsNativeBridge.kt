package app.purecipes.feature.analytics.domain.runtime

object IosAnalyticsNativeBridge {

	private var initializeMixpanelHandler: ((String, String) -> Unit)? = null
	private var trackMixpanelEventHandler: ((String, String) -> Unit)? = null
	private var setMixpanelTrackingEnabledHandler: ((Boolean) -> Unit)? = null
	private var setMixpanelUserIdHandler: ((String?) -> Unit)? = null
	private var registerMixpanelSuperPropertiesHandler: ((String) -> Unit)? = null
	private var showConsentFormHandler: (() -> Unit)? = null
	private var refreshConsentHandler: ((String, (String) -> Unit) -> Unit)? = null
	private var startConsentObserverHandler: ((String, (String) -> Unit) -> Unit)? = null

	fun registerMixpanelHandlers(
		initialize: (String, String) -> Unit,
		trackEvent: (String, String) -> Unit,
		setTrackingEnabled: (Boolean) -> Unit,
		setUserId: (String?) -> Unit,
		registerSuperProperties: (String) -> Unit,
	) {
		initializeMixpanelHandler = initialize
		trackMixpanelEventHandler = trackEvent
		setMixpanelTrackingEnabledHandler = setTrackingEnabled
		setMixpanelUserIdHandler = setUserId
		registerMixpanelSuperPropertiesHandler = registerSuperProperties
	}

	fun registerConsentHandlers(
		showConsentForm: () -> Unit,
		refreshConsent: (String, (String) -> Unit) -> Unit,
		startObserving: (String, (String) -> Unit) -> Unit,
	) {
		showConsentFormHandler = showConsentForm
		refreshConsentHandler = refreshConsent
		startConsentObserverHandler = startObserving
	}

	fun initializeMixpanel(token: String, serverUrl: String) {
		initializeMixpanelHandler?.invoke(token, serverUrl)
	}

	fun trackMixpanelEvent(eventName: String, propertiesJson: String) {
		trackMixpanelEventHandler?.invoke(eventName, propertiesJson)
	}

	fun setMixpanelTrackingEnabled(isEnabled: Boolean) {
		setMixpanelTrackingEnabledHandler?.invoke(isEnabled)
	}

	fun setMixpanelUserId(userId: String?) {
		setMixpanelUserIdHandler?.invoke(userId)
	}

	fun registerMixpanelSuperProperties(propertiesJson: String) {
		registerMixpanelSuperPropertiesHandler?.invoke(propertiesJson)
	}

	fun showConsentForm() {
		showConsentFormHandler?.invoke()
	}

	fun refreshConsent(settingsId: String, onResult: (String) -> Unit) {
		refreshConsentHandler?.invoke(settingsId, onResult)
			?: onResult(ConsentBridgeState.UNKNOWN.name)
	}

	fun startObservingConsent(settingsId: String, onUpdate: (String) -> Unit) {
		startConsentObserverHandler?.invoke(settingsId, onUpdate)
	}
}

enum class ConsentBridgeState {
	NOT_REQUIRED,
	UNKNOWN,
	REQUIRED,
	OBTAINED,
	DENIED,
}

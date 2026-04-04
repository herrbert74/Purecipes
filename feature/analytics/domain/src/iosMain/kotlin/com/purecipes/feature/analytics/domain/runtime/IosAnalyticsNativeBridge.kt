package com.purecipes.feature.analytics.domain.runtime

object IosAnalyticsNativeBridge {
	private var initializeMixpanelHandler: ((String) -> Unit)? = null
	private var trackMixpanelEventHandler: ((String, String) -> Unit)? = null
	private var setMixpanelTrackingEnabledHandler: ((Boolean) -> Unit)? = null
	private var setMixpanelUserIdHandler: ((String?) -> Unit)? = null
	private var showConsentFormHandler: (() -> Unit)? = null

	fun registerMixpanelHandlers(
		initialize: (String) -> Unit,
		trackEvent: (String, String) -> Unit,
		setTrackingEnabled: (Boolean) -> Unit,
		setUserId: (String?) -> Unit,
	) {
		initializeMixpanelHandler = initialize
		trackMixpanelEventHandler = trackEvent
		setMixpanelTrackingEnabledHandler = setTrackingEnabled
		setMixpanelUserIdHandler = setUserId
	}

	fun registerConsentHandlers(showConsentForm: () -> Unit) {
		showConsentFormHandler = showConsentForm
	}

	fun initializeMixpanel(token: String) {
		initializeMixpanelHandler?.invoke(token)
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

	fun showConsentForm() {
		showConsentFormHandler?.invoke()
	}
}

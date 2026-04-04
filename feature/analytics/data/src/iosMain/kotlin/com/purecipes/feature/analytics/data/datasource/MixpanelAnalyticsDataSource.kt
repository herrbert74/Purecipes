package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.feature.analytics.domain.runtime.IosAnalyticsNativeBridge
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()
	private var isInitialized = false
	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled) {
			return
		}
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.trackMixpanelEvent(eventName, properties.toAnalyticsJson())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.setMixpanelTrackingEnabled(isEnabled)
	}

	actual override fun setUserId(userId: String?) {
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.setMixpanelUserId(userId)
	}

	private fun initializeIfNeeded() {
		if (isInitialized) {
			return
		}
		IosAnalyticsNativeBridge.initializeMixpanel(token)
		isInitialized = true
	}
}

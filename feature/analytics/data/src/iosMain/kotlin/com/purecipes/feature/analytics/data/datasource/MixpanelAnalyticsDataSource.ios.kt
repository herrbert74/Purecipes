package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled) {
			return
		}
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
	}

	actual override fun setUserId(userId: String?) {
	}
}
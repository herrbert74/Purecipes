package com.purecipes.feature.analytics.data.datasource

import cocoapods.FirebaseAnalytics.FIRAnalytics
import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class Ga4AnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	init {
		setTrackingEnabled(isEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank())
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		FIRAnalytics.logEventWithName(eventName, properties.toParameters())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		FIRAnalytics.setAnalyticsCollectionEnabled(isEnabled)
	}

	actual override fun setUserId(userId: String?) {
		FIRAnalytics.setUserID(userId)
	}
}

private fun Map<String, AnalyticsValue>.toParameters(): Map<Any?, *> {
	return mapValues { (_, value) ->
		when (value) {
			is AnalyticsValue.BooleanValue -> value.value
			is AnalyticsValue.NumberValue -> value.value
			is AnalyticsValue.TextValue -> value.value
		}
	}
}

package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal expect class Ga4AnalyticsDataSource(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {
	override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>)

	override fun setTrackingEnabled(isEnabled: Boolean)

	override fun setUserId(userId: String?)
}
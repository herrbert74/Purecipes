package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesConfig

internal actual class Ga4AnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) = Unit

	actual override fun setTrackingEnabled(isEnabled: Boolean) = Unit

	actual override fun setUserId(userId: String?) = Unit
}

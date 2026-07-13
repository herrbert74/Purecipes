package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesConfig

expect class MixpanelAnalyticsDataSource(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>)

	override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>)

	override fun setGlobalProperties(properties: Map<String, AnalyticsValue>)

	override fun setTrackingEnabled(isEnabled: Boolean)

	override fun setUserId(userId: String?)
}

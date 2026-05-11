package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue

internal interface AnalyticsDataSource {
	fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>)

	fun setTrackingEnabled(isEnabled: Boolean)

	fun setUserId(userId: String?)
}

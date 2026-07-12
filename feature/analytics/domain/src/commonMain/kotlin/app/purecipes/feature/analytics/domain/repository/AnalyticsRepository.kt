package app.purecipes.feature.analytics.domain.repository

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsValue

interface AnalyticsRepository {

	fun trackEvent(event: AnalyticsEvent)

	fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>)

	fun setGlobalProperties(properties: Map<String, AnalyticsValue>)

	fun setUserId(userId: String?)
}

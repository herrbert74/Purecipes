package app.purecipes.feature.analytics.domain.repository

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent

interface AnalyticsRepository {
	fun trackEvent(event: AnalyticsEvent)

	fun setUserId(userId: String?)
}

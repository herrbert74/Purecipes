package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository

class FakeAnalyticsRepository : AnalyticsRepository {

	val trackedEvents = mutableListOf<AnalyticsEvent>()
	var lastUserId: String? = null

	override fun trackEvent(event: AnalyticsEvent) {
		trackedEvents += event
	}

	override fun setUserId(userId: String?) {
		lastUserId = userId
	}
}

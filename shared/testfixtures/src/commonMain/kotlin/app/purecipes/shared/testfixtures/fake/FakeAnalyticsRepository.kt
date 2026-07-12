package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository

class FakeAnalyticsRepository : AnalyticsRepository {

	val trackedEvents = mutableListOf<AnalyticsEvent>()
	val trackedScreenViews = mutableListOf<TrackedScreenView>()
	var lastUserId: String? = null

	override fun trackEvent(event: AnalyticsEvent) {
		trackedEvents += event
	}

	override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		trackedScreenViews += TrackedScreenView(screenName = screenName, properties = properties)
	}

	override fun setUserId(userId: String?) {
		lastUserId = userId
	}

	data class TrackedScreenView(
		val screenName: String,
		val properties: Map<String, AnalyticsValue>,
	)
}

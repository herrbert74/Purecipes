package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository

class FakeAnalyticsRepository : AnalyticsRepository {

	val trackedEvents = mutableListOf<AnalyticsEvent>()
	val trackedScreenViews = mutableListOf<TrackedScreenView>()
	val globalProperties = linkedMapOf<String, AnalyticsValue>()
	var lastUserId: String? = null

	override fun trackEvent(event: AnalyticsEvent) {
		trackedEvents += event
	}

	override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		trackedScreenViews += TrackedScreenView(screenName = screenName, properties = properties)
	}

	override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
		globalProperties.putAll(properties)
	}

	override fun setUserId(userId: String?) {
		lastUserId = userId
	}

	data class TrackedScreenView(
		val screenName: String,
		val properties: Map<String, AnalyticsValue>,
	)
}

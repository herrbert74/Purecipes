package com.purecipes.feature.analytics.domain.repository

import com.purecipes.feature.analytics.domain.model.AnalyticsEvent

interface AnalyticsRepository {
	fun trackEvent(event: AnalyticsEvent)

	fun setUserId(userId: String?)
}

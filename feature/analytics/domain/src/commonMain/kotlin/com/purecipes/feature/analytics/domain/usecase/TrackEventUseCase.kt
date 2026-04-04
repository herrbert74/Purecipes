package com.purecipes.feature.analytics.domain.usecase

import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.repository.AnalyticsRepository

class TrackEventUseCase(
	private val analyticsRepository: AnalyticsRepository,
) {
	operator fun invoke(event: AnalyticsEvent) {
		analyticsRepository.trackEvent(event)
	}
}

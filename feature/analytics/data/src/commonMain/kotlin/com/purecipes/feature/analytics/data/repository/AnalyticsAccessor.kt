package com.purecipes.feature.analytics.data.repository

import com.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.model.allowsAnalytics
import com.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import com.purecipes.feature.analytics.domain.repository.ConsentRepository

internal class AnalyticsAccessor(
	private val analyticsDataSources: List<AnalyticsDataSource>,
	private val consentRepository: ConsentRepository,
) : AnalyticsRepository {

	override fun trackEvent(event: AnalyticsEvent) {
		val isEnabled = consentRepository.currentConsentState().allowsAnalytics()
		analyticsDataSources.forEach { it.setTrackingEnabled(isEnabled) }
		if (!isEnabled) {
			return
		}
		analyticsDataSources.forEach { it.trackEvent(event.eventName, event.properties) }
	}

	override fun setUserId(userId: String?) {
		val isEnabled = consentRepository.currentConsentState().allowsAnalytics()
		analyticsDataSources.forEach {
			it.setTrackingEnabled(isEnabled)
			it.setUserId(userId)
		}
	}
}
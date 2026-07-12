package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.model.allowsAnalytics
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AnalyticsAccessor internal constructor(
	private val analyticsDataSources: Set<AnalyticsDataSource>,
	private val consentRepository: ConsentRepository,
	observationScope: CoroutineScope,
) : AnalyticsRepository {

	@Inject
	constructor(
		analyticsDataSources: Set<AnalyticsDataSource>,
		consentRepository: ConsentRepository,
	) : this(
		analyticsDataSources = analyticsDataSources,
		consentRepository = consentRepository,
		observationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
	)

	init {
		applyTrackingEnabled(consentRepository.currentConsentState())
		observationScope.launch {
			consentRepository.observeConsentState().collect { consentState ->
				applyTrackingEnabled(consentState)
			}
		}
	}

	override fun trackEvent(event: AnalyticsEvent) {
		if (!consentRepository.currentConsentState().allowsAnalytics()) {
			return
		}
		analyticsDataSources.forEach { it.trackEvent(event.eventName, event.properties) }
	}

	override fun setUserId(userId: String?) {
		val isEnabled = consentRepository.currentConsentState().allowsAnalytics()
		analyticsDataSources.forEach {
			it.setUserId(if (isEnabled) userId else null)
		}
	}

	private fun applyTrackingEnabled(consentState: ConsentState) {
		val isEnabled = consentState.allowsAnalytics()
		analyticsDataSources.forEach { it.setTrackingEnabled(isEnabled) }
	}
}

package app.purecipes.feature.analytics.data.repository

import app.purecipes.feature.analytics.data.datasource.AnalyticsDataSource
import app.purecipes.feature.analytics.data.platform.analyticsPlatformValue
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsGlobalProperty
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.model.allowsAnalytics
import app.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import app.purecipes.feature.analytics.domain.repository.ConsentRepository
import app.purecipes.shared.data.config.PurecipesConfig
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
	private val purecipesConfig: PurecipesConfig,
	observationScope: CoroutineScope,
) : AnalyticsRepository {

	private val globalProperties = linkedMapOf<String, AnalyticsValue>()

	@Inject
	constructor(
		analyticsDataSources: Set<AnalyticsDataSource>,
		consentRepository: ConsentRepository,
		purecipesConfig: PurecipesConfig,
	) : this(
		analyticsDataSources = analyticsDataSources,
		consentRepository = consentRepository,
		purecipesConfig = purecipesConfig,
		observationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
	)

	init {
		applyTrackingEnabled(consentRepository.currentConsentState())
		setGlobalProperties(
			mapOf(
				AnalyticsGlobalProperty.ENVIRONMENT to AnalyticsValue.TextValue(purecipesConfig.environment()),
				AnalyticsGlobalProperty.PLATFORM to AnalyticsValue.TextValue(analyticsPlatformValue()),
				AnalyticsGlobalProperty.APP_VERSION to AnalyticsValue.TextValue(purecipesConfig.versionName()),
			),
		)
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

	override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		if (!consentRepository.currentConsentState().allowsAnalytics()) {
			return
		}
		analyticsDataSources.forEach { it.trackScreenView(screenName, properties) }
	}

	override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
		globalProperties.putAll(properties)
		val snapshot = globalProperties.toMap()
		analyticsDataSources.forEach { it.setGlobalProperties(snapshot) }
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

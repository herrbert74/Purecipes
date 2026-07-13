package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import swiftPMImport.Purecipes.feature.analytics.feature.analytics.data.FIRAnalytics

private const val SCREEN_VIEW_EVENT_NAME = "screen_view"
private const val SCREEN_NAME_PROPERTY = "screen_name"

@Inject
@ContributesIntoSet(AppScope::class)
actual class Ga4AnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	init {
		setTrackingEnabled(isEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank())
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		FIRAnalytics.logEventWithName(eventName, properties.toParameters())
	}

	actual override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		val params = properties.toMutableMap()
		params[SCREEN_NAME_PROPERTY] = AnalyticsValue.TextValue(screenName)
		FIRAnalytics.logEventWithName(SCREEN_VIEW_EVENT_NAME, params.toParameters())
	}

	actual override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
		FIRAnalytics.setDefaultEventParameters(properties.toParameters())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		FIRAnalytics.setAnalyticsCollectionEnabled(isEnabled)
	}

	actual override fun setUserId(userId: String?) {
		FIRAnalytics.setUserID(userId)
	}
}

private fun Map<String, AnalyticsValue>.toParameters(): Map<Any?, *> {
	return mapValues { (_, value) ->
		when (value) {
			is AnalyticsValue.BooleanValue -> value.value
			is AnalyticsValue.NumberValue -> value.value
			is AnalyticsValue.TextValue -> value.value
		}
	}
}

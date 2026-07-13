package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) = Unit

	actual override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) = Unit

	actual override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) = Unit

	actual override fun setTrackingEnabled(isEnabled: Boolean) = Unit

	actual override fun setUserId(userId: String?) = Unit
}

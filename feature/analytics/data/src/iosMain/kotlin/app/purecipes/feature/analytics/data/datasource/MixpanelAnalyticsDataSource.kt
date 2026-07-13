package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.feature.analytics.domain.runtime.IosAnalyticsNativeBridge
import app.purecipes.shared.data.config.PurecipesConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()
	private var isInitialized = false
	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled) {
			return
		}
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.trackMixpanelEvent(eventName, properties.toAnalyticsJson())
	}

	actual override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled) {
			return
		}
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.trackMixpanelEvent(screenName, properties.toAnalyticsJson())
	}

	actual override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.registerMixpanelSuperProperties(properties.toAnalyticsJson())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.setMixpanelTrackingEnabled(isEnabled)
	}

	actual override fun setUserId(userId: String?) {
		if (token.isBlank()) {
			return
		}
		initializeIfNeeded()
		IosAnalyticsNativeBridge.setMixpanelUserId(userId)
	}

	private fun initializeIfNeeded() {
		if (isInitialized) {
			return
		}
		IosAnalyticsNativeBridge.initializeMixpanel(token, MIXPANEL_SERVER_URL)
		isInitialized = true
	}
}

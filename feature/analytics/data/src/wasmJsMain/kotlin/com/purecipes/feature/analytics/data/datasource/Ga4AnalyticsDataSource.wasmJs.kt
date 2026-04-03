package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class Ga4AnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val measurementId = purecipesConfig.gaMeasurementId().orEmpty()
	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	init {
		ga4EnsureInitialized(measurementId)
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled || measurementId.isBlank()) {
			return
		}
		ga4TrackEvent(eventName, properties.toAnalyticsJson())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
	}

	actual override fun setUserId(userId: String?) {
		if (measurementId.isBlank()) {
			return
		}
		ga4SetUserId(userId)
	}
}

@JsFun("""
	(measurementId) => {
		if (!measurementId || !globalThis.document || globalThis.__purecipesGaInitialized) {
			return;
		}
		globalThis.dataLayer = globalThis.dataLayer || [];
		globalThis.gtag = function(){ globalThis.dataLayer.push(arguments); };
		const script = document.createElement('script');
		script.async = true;
		script.src = 'https://www.googletagmanager.com/gtag/js?id=' + encodeURIComponent(measurementId);
		document.head.appendChild(script);
		globalThis.gtag('js', new Date());
		globalThis.gtag('config', measurementId);
		globalThis.__purecipesGaInitialized = true;
	}
""")
private external fun ga4EnsureInitialized(measurementId: String)

@JsFun("""
	(eventName, propertiesJson) => {
		if (!globalThis.gtag) {
			return;
		}
		globalThis.gtag('event', eventName, propertiesJson ? JSON.parse(propertiesJson) : {});
	}
""")
private external fun ga4TrackEvent(eventName: String, propertiesJson: String)

@JsFun("""
	(userId) => {
		if (!globalThis.gtag) {
			return;
		}
		globalThis.gtag('set', 'user_properties', userId ? { user_id: userId } : {});
	}
""")
private external fun ga4SetUserId(userId: String?)
package com.purecipes.feature.analytics.data.datasource

import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()
	private var isTrackingEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	init {
		mixpanelEnsureInitialized(token)
		setTrackingEnabled(isTrackingEnabled)
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (!isTrackingEnabled || token.isBlank()) {
			return
		}
		mixpanelTrackEvent(eventName, properties.toAnalyticsJson())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		isTrackingEnabled = isEnabled
		if (token.isBlank()) {
			return
		}
		if (isEnabled) {
			mixpanelOptInTracking()
		} else {
			mixpanelOptOutTracking()
		}
	}

	actual override fun setUserId(userId: String?) {
		if (token.isBlank()) {
			return
		}
		if (userId.isNullOrBlank()) {
			mixpanelReset()
		} else {
			mixpanelIdentify(userId)
		}
	}
}

@JsFun("""
	(token) => {
		if (!token || !globalThis.document || globalThis.__purecipesMixpanelInit) {
			return;
		}
		const script = document.createElement('script');
		script.async = true;
		script.src = 'https://cdn.mxpnl.com/libs/mixpanel-2-latest.min.js';
		script.onload = () => {
			globalThis.mixpanel.init(token, { persistence: 'localStorage', autocapture: false, track_pageview: false, opt_out_tracking_by_default: true });
		};
		document.head.appendChild(script);
		globalThis.__purecipesMixpanelInit = true;
	}
""")
private external fun mixpanelEnsureInitialized(token: String)

@JsFun("""
	(eventName, propertiesJson) => {
		if (!globalThis.mixpanel) {
			return;
		}
		globalThis.mixpanel.track(eventName, propertiesJson ? JSON.parse(propertiesJson) : {});
	}
""")
private external fun mixpanelTrackEvent(eventName: String, propertiesJson: String)

@JsFun("""
	(userId) => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.identify(userId);
		}
	}
""")
private external fun mixpanelIdentify(userId: String)

@JsFun("""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.reset();
		}
	}
""")
private external fun mixpanelReset()

@JsFun("""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.opt_in_tracking();
		}
	}
""")
private external fun mixpanelOptInTracking()

@JsFun("""
	() => {
		if (globalThis.mixpanel) {
			globalThis.mixpanel.opt_out_tracking();
		}
	}
""")
private external fun mixpanelOptOutTracking()
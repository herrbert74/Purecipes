package app.purecipes.feature.analytics.data.datasource

import com.mixpanel.android.mpmetrics.MixpanelAPI
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesConfig
import org.json.JSONObject

internal actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()

	private val mixpanel by lazy {
		MixpanelAPI.getInstance(
			AnalyticsAndroidRuntime.applicationContext,
			token,
			true,
			false,
		)
	}

	init {
		setTrackingEnabled(isEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank())
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		if (token.isBlank()) {
			return
		}
		mixpanel.track(eventName, properties.toJsonObject())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		if (token.isBlank()) {
			return
		}
		if (isEnabled) {
			mixpanel.optInTracking()
		} else {
			mixpanel.optOutTracking()
		}
	}

	actual override fun setUserId(userId: String?) {
		if (token.isBlank()) {
			return
		}
		if (userId.isNullOrBlank()) {
			mixpanel.reset()
		} else {
			mixpanel.identify(userId, false)
		}
	}
}

private fun Map<String, AnalyticsValue>.toJsonObject(): JSONObject {
	return JSONObject().also { jsonObject ->
		forEach { (key, value) ->
			when (value) {
				is AnalyticsValue.BooleanValue -> jsonObject.put(key, value.value)
				is AnalyticsValue.NumberValue -> jsonObject.put(key, value.value)
				is AnalyticsValue.TextValue -> jsonObject.put(key, value.value)
			}
		}
	}
}

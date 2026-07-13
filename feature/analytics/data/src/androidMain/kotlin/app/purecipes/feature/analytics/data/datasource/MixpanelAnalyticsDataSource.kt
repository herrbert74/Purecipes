package app.purecipes.feature.analytics.data.datasource

import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import app.purecipes.feature.analytics.domain.model.AnalyticsValue
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.mixpanel.android.mpmetrics.MixpanelOptions
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import org.json.JSONObject

@Inject
@ContributesIntoSet(AppScope::class)
actual class MixpanelAnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val token = purecipesConfig.mixpanelProjectToken().orEmpty()
	private val enableDebugLogging = purecipesConfig.buildType() != PurecipesBuildType.RELEASE
	private val optOutByDefault = !purecipesConfig.usercentricsSettingsId().isNullOrBlank()

	private val mixpanel by lazy {
		val options = MixpanelOptions.Builder()
			.optOutTrackingDefault(optOutByDefault)
			.serverURL(MIXPANEL_SERVER_URL)
			.build()
		MixpanelAPI.getInstance(
			AnalyticsAndroidRuntime.applicationContext,
			token,
			true,
			options,
		).also { api ->
			if (enableDebugLogging) {
				api.setEnableLogging(true)
			}
		}
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

	actual override fun trackScreenView(screenName: String, properties: Map<String, AnalyticsValue>) {
		if (token.isBlank()) {
			return
		}
		mixpanel.track(screenName, properties.toJsonObject())
	}

	actual override fun setGlobalProperties(properties: Map<String, AnalyticsValue>) {
		if (token.isBlank()) {
			return
		}
		mixpanel.registerSuperProperties(properties.toJsonObject())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		if (token.isBlank()) {
			return
		}
		if (isEnabled) {
			mixpanel.optInTracking()
		} else {
			mixpanel.flush()
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

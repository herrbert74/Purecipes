package com.purecipes.feature.analytics.data.datasource

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.purecipes.feature.analytics.domain.model.AnalyticsValue
import com.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import com.purecipes.shared.data.config.PurecipesConfig

internal actual class Ga4AnalyticsDataSource actual constructor(
	purecipesConfig: PurecipesConfig,
) : AnalyticsDataSource {

	private val firebaseAnalytics by lazy {
		FirebaseAnalytics.getInstance(AnalyticsAndroidRuntime.applicationContext)
	}

	init {
		setTrackingEnabled(isEnabled = purecipesConfig.usercentricsSettingsId().isNullOrBlank())
	}

	actual override fun trackEvent(eventName: String, properties: Map<String, AnalyticsValue>) {
		firebaseAnalytics.logEvent(eventName, properties.toBundle())
	}

	actual override fun setTrackingEnabled(isEnabled: Boolean) {
		firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
	}

	actual override fun setUserId(userId: String?) {
		firebaseAnalytics.setUserId(userId)
	}
}

private fun Map<String, AnalyticsValue>.toBundle(): Bundle {
	val bundle = Bundle()
	forEach { (key, value) ->
		when (value) {
			is AnalyticsValue.BooleanValue -> bundle.putBoolean(key, value.value)
			is AnalyticsValue.NumberValue -> bundle.putLong(key, value.value)
			is AnalyticsValue.TextValue -> bundle.putString(key, value.value)
		}
	}
	return bundle
}
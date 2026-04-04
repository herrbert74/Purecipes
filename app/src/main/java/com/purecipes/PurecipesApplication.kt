package com.purecipes

import android.app.Application
import com.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime

class PurecipesApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		AnalyticsAndroidRuntime.initialize(this)
	}
}

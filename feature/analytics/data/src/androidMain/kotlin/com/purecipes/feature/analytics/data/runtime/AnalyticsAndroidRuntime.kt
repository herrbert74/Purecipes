package com.purecipes.feature.analytics.data.runtime

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

object AnalyticsAndroidRuntime {
	private var activityReference = WeakReference<Activity>(null)

	lateinit var applicationContext: Context
		private set

	fun initialize(context: Context) {
		applicationContext = context.applicationContext
	}

	fun onActivityStarted(activity: Activity) {
		activityReference = WeakReference(activity)
	}

	fun onActivityStopped(activity: Activity) {
		if (activityReference.get() === activity) {
			activityReference = WeakReference(null)
		}
	}

	fun currentActivity(): Activity? = activityReference.get()
}
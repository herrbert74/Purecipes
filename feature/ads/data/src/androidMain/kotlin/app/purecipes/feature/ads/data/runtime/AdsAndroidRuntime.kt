package app.purecipes.feature.ads.data.runtime

import android.app.Activity
import android.content.Context
import java.lang.ref.WeakReference

object AdsAndroidRuntime {
	private var activityReference = WeakReference<Activity>(null)

	private var applicationContext: Context? = null

	fun initialize(context: Context) {
		applicationContext = context.applicationContext
	}

	fun applicationContextOrNull(): Context? = applicationContext

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

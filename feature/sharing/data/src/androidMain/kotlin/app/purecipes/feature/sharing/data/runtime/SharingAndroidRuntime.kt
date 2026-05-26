package app.purecipes.feature.sharing.data.runtime

import android.app.Activity
import android.content.Intent
import java.lang.ref.WeakReference

object SharingAndroidRuntime {

	private var activityReference = WeakReference<Activity>(null)

	fun onActivityStarted(activity: Activity) {
		activityReference = WeakReference(activity)
	}

	fun onActivityStopped(activity: Activity) {
		if (activityReference.get() === activity) {
			activityReference = WeakReference(null)
		}
	}

	fun shareText(text: String, title: String?) {
		val activity = activityReference.get() ?: return
		val shareIntent = Intent(Intent.ACTION_SEND).apply {
			type = "text/plain"
			putExtra(Intent.EXTRA_TEXT, text)
			title?.let { putExtra(Intent.EXTRA_TITLE, it) }
		}
		val chooser = Intent.createChooser(shareIntent, title)
		activity.startActivity(chooser)
	}
}

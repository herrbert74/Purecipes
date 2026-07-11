package app.purecipes

import android.app.Application
import app.purecipes.feature.ads.data.runtime.AdsAndroidRuntime
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.extensions.initialize
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.push.firebase.FirebasePush

class PurecipesApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		KMPNotifier.initialize(
			context = this,
			configuration = NotificationPlatformConfiguration.Android(
				notificationIconResId = R.drawable.placeholder,
				showPushNotification = true,
			),
			FirebasePush,
		)
		AnalyticsAndroidRuntime.initialize(this)
		AdsAndroidRuntime.initialize(this)
	}
}

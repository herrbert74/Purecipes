package app.purecipes

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import app.purecipes.feature.analytics.data.runtime.AnalyticsAndroidRuntime

class PurecipesApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		NotifierManager.initialize(
			NotificationPlatformConfiguration.Android(
				notificationIconResId = R.drawable.placeholder,
				showPushNotification = true,
			)
		)
		AnalyticsAndroidRuntime.initialize(this)
	}
}

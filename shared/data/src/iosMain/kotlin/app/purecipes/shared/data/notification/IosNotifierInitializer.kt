package app.purecipes.shared.data.notification

import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.push.firebase.FirebasePush

object IosNotifierInitializer {
	fun initialize() {
		KMPNotifier.initialize(
			configuration = NotificationPlatformConfiguration.Ios(
				showPushNotification = true,
				askNotificationPermissionOnStart = true
			),
			FirebasePush,
		)
	}
}

package com.purecipes.shared.data.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

object IosNotifierInitializer {
	fun initialize() {
		NotifierManager.initialize(
			configuration = NotificationPlatformConfiguration.Ios(
				showPushNotification = true,
				askNotificationPermissionOnStart = true
			)
		)
	}
}

package com.purecipes.feature.settings.domain.usecase

import com.purecipes.shared.domain.notification.NotificationData
import com.purecipes.shared.domain.notification.NotificationManager

class SendTestNotificationUseCase(
	private val notificationManager: NotificationManager,
) {
	operator fun invoke(title: String, body: String) {
		notificationManager.sendLocalNotification(
			NotificationData(title = title, body = body)
		)
	}
}

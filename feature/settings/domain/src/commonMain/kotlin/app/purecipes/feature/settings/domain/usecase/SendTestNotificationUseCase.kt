package app.purecipes.feature.settings.domain.usecase

import app.purecipes.shared.domain.notification.NotificationData
import app.purecipes.shared.domain.notification.NotificationManager
import dev.zacsweers.metro.Inject

@Inject
class SendTestNotificationUseCase(
	private val notificationManager: NotificationManager,
) {
	operator fun invoke(title: String, body: String) {
		notificationManager.sendLocalNotification(
			NotificationData(title = title, body = body)
		)
	}
}

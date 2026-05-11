package app.purecipes.feature.settings.domain.usecase

import app.purecipes.shared.domain.notification.NotificationManager

class InitializeNotificationsUseCase(
	private val notificationManager: NotificationManager,
) {
	suspend operator fun invoke() {
		notificationManager.initialize()
	}
}

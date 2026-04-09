package com.purecipes.feature.settings.domain.usecase

import com.purecipes.shared.domain.notification.NotificationManager

class InitializeNotificationsUseCase(
	private val notificationManager: NotificationManager,
) {
	suspend operator fun invoke() {
		notificationManager.initialize()
	}
}

package com.purecipes.feature.settings.domain.usecase

import com.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import com.purecipes.shared.domain.model.NotificationPreferences
import com.purecipes.shared.domain.notification.NotificationManager
import kotlinx.coroutines.flow.Flow

class ObserveNotificationPreferencesUseCase(
	private val repository: NotificationPreferencesRepository,
) {
	operator fun invoke(): Flow<NotificationPreferences> = repository.observeNotificationPreferences()
}

class SaveNotificationPreferencesUseCase(
	private val repository: NotificationPreferencesRepository,
	private val notificationManager: NotificationManager,
) {
	suspend operator fun invoke(
		preferences: NotificationPreferences,
	) {
		repository.saveNotificationPreferences(preferences)

		syncTopic("timers", preferences.enableAll && preferences.enableTimers)
		syncTopic("social", preferences.enableAll && preferences.enableSocial)
		syncTopic("updates", preferences.enableAll && preferences.enableUpdates)
	}

	private suspend fun syncTopic(topic: String, enable: Boolean) {
		if (enable) {
			notificationManager.subscribeToTopic(topic)
		} else {
			notificationManager.unsubscribeFromTopic(topic)
		}
	}
}

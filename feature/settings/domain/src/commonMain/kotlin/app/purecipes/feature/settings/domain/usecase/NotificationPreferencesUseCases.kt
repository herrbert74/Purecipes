package app.purecipes.feature.settings.domain.usecase

import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.domain.notification.NotificationManager
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveNotificationPreferencesUseCase(
	private val repository: NotificationPreferencesRepository,
) {
	operator fun invoke(): Flow<NotificationPreferences> = repository.observeNotificationPreferences()
}

@Inject
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

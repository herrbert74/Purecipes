package app.purecipes.feature.settings.data.datasource

import app.purecipes.shared.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesDataSource {
	fun observeNotificationPreferences(): Flow<NotificationPreferences>
	fun saveNotificationPreferences(preferences: NotificationPreferences)
}

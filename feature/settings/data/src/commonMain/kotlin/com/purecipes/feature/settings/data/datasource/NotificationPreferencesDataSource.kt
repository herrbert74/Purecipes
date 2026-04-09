package com.purecipes.feature.settings.data.datasource

import com.purecipes.shared.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesDataSource {
	fun observeNotificationPreferences(): Flow<NotificationPreferences>
	fun saveNotificationPreferences(preferences: NotificationPreferences)
}

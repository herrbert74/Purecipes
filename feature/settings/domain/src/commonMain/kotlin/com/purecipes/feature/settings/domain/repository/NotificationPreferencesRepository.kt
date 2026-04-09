package com.purecipes.feature.settings.domain.repository

import com.purecipes.shared.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationPreferencesRepository {
	fun observeNotificationPreferences(): Flow<NotificationPreferences>
	suspend fun saveNotificationPreferences(preferences: NotificationPreferences)
}

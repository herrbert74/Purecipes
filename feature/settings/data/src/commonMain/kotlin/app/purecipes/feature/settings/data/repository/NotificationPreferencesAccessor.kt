package app.purecipes.feature.settings.data.repository

import app.purecipes.feature.settings.data.datasource.NotificationPreferencesDataSource
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

class NotificationPreferencesAccessor(
	private val localDataSource: NotificationPreferencesDataSource,
) : NotificationPreferencesRepository {

	override fun observeNotificationPreferences(): Flow<NotificationPreferences> =
		localDataSource.observeNotificationPreferences()

	override suspend fun saveNotificationPreferences(preferences: NotificationPreferences) {
		localDataSource.saveNotificationPreferences(preferences)
	}
}

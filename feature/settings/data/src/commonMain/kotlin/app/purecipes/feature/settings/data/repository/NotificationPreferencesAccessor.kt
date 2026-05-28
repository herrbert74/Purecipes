package app.purecipes.feature.settings.data.repository

import app.purecipes.feature.settings.data.datasource.NotificationPreferencesDataSource
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.domain.model.NotificationPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class)
class NotificationPreferencesAccessor(
	private val localDataSource: NotificationPreferencesDataSource,
) : NotificationPreferencesRepository {

	override fun observeNotificationPreferences(): Flow<NotificationPreferences> =
		localDataSource.observeNotificationPreferences()

	override suspend fun saveNotificationPreferences(preferences: NotificationPreferences) {
		localDataSource.saveNotificationPreferences(preferences)
	}
}

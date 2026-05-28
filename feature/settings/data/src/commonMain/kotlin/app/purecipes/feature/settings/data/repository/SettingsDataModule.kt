package app.purecipes.feature.settings.data.repository

import app.purecipes.feature.settings.data.datasource.NotificationPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.SettingsNotificationPreferencesDataSource
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.data.notification.KmpNotificationManager
import app.purecipes.shared.domain.notification.NotificationManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsDataModule {

	@Provides
	fun provideNotificationPreferencesDataSource(): NotificationPreferencesDataSource {
		return SettingsNotificationPreferencesDataSource()
	}

	@Provides
	fun provideNotificationPreferencesRepository(
		localDataSource: NotificationPreferencesDataSource,
	): NotificationPreferencesRepository {
		return NotificationPreferencesAccessor(localDataSource)
	}

	@Provides
	fun provideNotificationManager(
		kmpNotificationManager: KmpNotificationManager,
	): NotificationManager {
		return kmpNotificationManager
	}
}

package app.purecipes.feature.settings.data.repository

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.feature.settings.data.datasource.MeasurementPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.MeasurementPreferencesRemoteDataSource
import app.purecipes.feature.settings.data.datasource.NotificationPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.PurecipesMeasurementPreferencesRemoteDataSource
import app.purecipes.feature.settings.data.datasource.SettingsMeasurementPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.SettingsNotificationPreferencesDataSource
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.notification.KmpNotificationManager
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.domain.notification.NotificationManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SettingsDataModule {

	@Provides
	fun provideMeasurementPreferencesDataSource(): MeasurementPreferencesDataSource {
		return SettingsMeasurementPreferencesDataSource()
	}

	@Provides
	fun provideMeasurementPreferencesRemoteDataSource(api: PurecipesApi): MeasurementPreferencesRemoteDataSource {
		return PurecipesMeasurementPreferencesRemoteDataSource(api)
	}

	@Provides
	fun provideMeasurementPreferencesRepository(
		localDataSource: MeasurementPreferencesDataSource,
		remoteDataSource: MeasurementPreferencesRemoteDataSource,
		sessionTokenStore: SessionTokenStore,
	): MeasurementPreferencesRepository {
		return MeasurementPreferencesAccessor(
			localDataSource = localDataSource,
			remoteDataSource = remoteDataSource,
			sessionTokenStore = sessionTokenStore,
		)
	}

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

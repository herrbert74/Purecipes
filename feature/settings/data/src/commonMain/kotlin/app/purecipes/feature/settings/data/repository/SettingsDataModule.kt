package app.purecipes.feature.settings.data.repository

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.settings.data.datasource.MeasurementPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.MeasurementPreferencesRemoteDataSource
import app.purecipes.feature.settings.data.datasource.NotificationPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.PurecipesMeasurementPreferencesRemoteDataSource
import app.purecipes.feature.settings.data.datasource.SettingsMeasurementPreferencesDataSource
import app.purecipes.feature.settings.data.datasource.SettingsNotificationPreferencesDataSource
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.feature.settings.domain.usecase.InitializeNotificationsUseCase
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
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
	fun provideObserveMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): ObserveMeasurementPreferencesUseCase {
		return ObserveMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideGetMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): GetMeasurementPreferencesUseCase {
		return GetMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideSaveMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): SaveMeasurementPreferencesUseCase {
		return SaveMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideResetMeasurementPreferencesUseCase(
		repository: MeasurementPreferencesRepository,
	): ResetMeasurementPreferencesUseCase {
		return ResetMeasurementPreferencesUseCase(repository)
	}

	@Provides
	fun provideMarkMeasurementMismatchSeenUseCase(
		repository: MeasurementPreferencesRepository,
	): MarkMeasurementMismatchSeenUseCase {
		return MarkMeasurementMismatchSeenUseCase(repository)
	}

	@Provides
	fun provideProcessRecipeDetailsForMeasurementPreferencesUseCase():
		ProcessRecipeDetailsForMeasurementPreferencesUseCase {
		return ProcessRecipeDetailsForMeasurementPreferencesUseCase()
	}

	@Provides
	fun provideFilterRecipesForMeasurementPreferencesUseCase(): FilterRecipesForMeasurementPreferencesUseCase {
		return FilterRecipesForMeasurementPreferencesUseCase()
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

	@Provides
	fun provideInitializeNotificationsUseCase(
		notificationManager: NotificationManager,
	): InitializeNotificationsUseCase {
		return InitializeNotificationsUseCase(notificationManager)
	}

	@Provides
	fun provideSendTestNotificationUseCase(
		notificationManager: NotificationManager,
	): SendTestNotificationUseCase {
		return SendTestNotificationUseCase(notificationManager)
	}

	@Provides
	fun provideObserveNotificationPreferencesUseCase(
		repository: NotificationPreferencesRepository,
	): ObserveNotificationPreferencesUseCase {
		return ObserveNotificationPreferencesUseCase(repository)
	}

	@Provides
	fun provideSaveNotificationPreferencesUseCase(
		repository: NotificationPreferencesRepository,
		notificationManager: NotificationManager,
	): SaveNotificationPreferencesUseCase {
		return SaveNotificationPreferencesUseCase(repository, notificationManager)
	}
}

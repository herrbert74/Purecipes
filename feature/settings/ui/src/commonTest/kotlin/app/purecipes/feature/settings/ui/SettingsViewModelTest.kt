package app.purecipes.feature.settings.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsMeasurementSystem
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SyncMeasurementPreferencesUseCase
import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.feature.subscription.domain.usecase.ObserveMonetisationDebugOverridesUseCase
import app.purecipes.feature.subscription.domain.usecase.SetAdsDisplayOverrideUseCase
import app.purecipes.feature.subscription.domain.usecase.SetPremiumStatusOverrideUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.NotificationPreferences
import app.purecipes.shared.domain.notification.NotificationData
import app.purecipes.shared.domain.notification.NotificationManager
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

	@Test
	fun `measurement preference change tracks measurement changed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(analyticsRepository = analyticsRepository)

		viewModel.onMeasurementPreferencesChange(
			MeasurementPreferences(preferredSystem = MeasurementSystem.IMPERIAL),
		)
		advanceUntilIdle()

		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.MeasurementChanged(system = AnalyticsMeasurementSystem.IMPERIAL),
		)
	}

	private fun createViewModel(
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	): SettingsViewModel {
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val notificationRepository = object : NotificationPreferencesRepository {
			private val preferences = MutableStateFlow(NotificationPreferences())

			override fun observeNotificationPreferences(): Flow<NotificationPreferences> = preferences

			override suspend fun saveNotificationPreferences(preferences: NotificationPreferences) {
				this.preferences.value = preferences
			}
		}
		val notificationManager = object : NotificationManager {
			override val token: Flow<String?> = MutableStateFlow(null)

			override suspend fun initialize() = Unit

			override suspend fun requestPermission(): Boolean = true

			override suspend fun subscribeToTopic(topic: String) = Unit

			override suspend fun unsubscribeFromTopic(topic: String) = Unit

			override fun sendLocalNotification(notification: NotificationData) = Unit
		}
		val monetisationRepository = FakeMonetisationDebugOverridesRepository()
		return SettingsViewModel(
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			resetMeasurementPreferences = ResetMeasurementPreferencesUseCase(measurementRepository),
			saveMeasurementPreferences = SaveMeasurementPreferencesUseCase(measurementRepository),
			syncMeasurementPreferences = SyncMeasurementPreferencesUseCase(measurementRepository),
			observeNotificationPreferences = ObserveNotificationPreferencesUseCase(notificationRepository),
			saveNotificationPreferences = SaveNotificationPreferencesUseCase(
				repository = notificationRepository,
				notificationManager = notificationManager,
			),
			sendTestNotification = SendTestNotificationUseCase(notificationManager),
			observeMonetisationDebugOverrides = ObserveMonetisationDebugOverridesUseCase(monetisationRepository),
			setPremiumStatusOverride = SetPremiumStatusOverrideUseCase(monetisationRepository),
			setAdsDisplayOverride = SetAdsDisplayOverrideUseCase(monetisationRepository),
			trackEvent = TrackEventUseCase(analyticsRepository),
			purecipesConfig = object : PurecipesConfig {
				override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

				override fun versionName(): String = "1.0.0"

				override fun versionCode(): Long = 1L
			},
		)
	}
}

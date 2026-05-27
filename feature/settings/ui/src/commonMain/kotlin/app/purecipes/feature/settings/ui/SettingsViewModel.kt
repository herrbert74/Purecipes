package app.purecipes.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.NotificationPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class SettingsViewModel(
	observeMeasurementPreferences: ObserveMeasurementPreferencesUseCase,
	private val resetMeasurementPreferences: ResetMeasurementPreferencesUseCase,
	private val saveMeasurementPreferences: SaveMeasurementPreferencesUseCase,
	observeNotificationPreferences: ObserveNotificationPreferencesUseCase,
	private val saveNotificationPreferences: SaveNotificationPreferencesUseCase,
	private val sendTestNotification: SendTestNotificationUseCase,
) : ViewModel() {

	val measurementPreferences: Flow<MeasurementPreferences> = observeMeasurementPreferences()

	val notificationPreferences: Flow<NotificationPreferences> = observeNotificationPreferences()

	fun onMeasurementPreferencesChange(preferences: MeasurementPreferences) {
		viewModelScope.launch {
			saveMeasurementPreferences(preferences)
		}
	}

	fun onResetMeasurementPreferences() {
		viewModelScope.launch {
			resetMeasurementPreferences()
		}
	}

	fun onNotificationPreferencesChange(preferences: NotificationPreferences) {
		viewModelScope.launch {
			saveNotificationPreferences(preferences)
		}
	}

	fun onSendTestNotification() {
		sendTestNotification(
			title = TEST_NOTIFICATION_TITLE,
			body = TEST_NOTIFICATION_BODY,
		)
	}

	private companion object {
		const val TEST_NOTIFICATION_TITLE = "Testing 1 2 3"
		const val TEST_NOTIFICATION_BODY = "Push notifications are working!"
	}
}

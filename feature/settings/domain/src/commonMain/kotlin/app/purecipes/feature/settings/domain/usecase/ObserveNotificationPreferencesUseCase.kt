package app.purecipes.feature.settings.domain.usecase

import app.purecipes.feature.settings.domain.repository.NotificationPreferencesRepository
import app.purecipes.shared.domain.model.NotificationPreferences
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveNotificationPreferencesUseCase(
	private val repository: NotificationPreferencesRepository,
) {

	operator fun invoke(): Flow<NotificationPreferences> = repository.observeNotificationPreferences()
}

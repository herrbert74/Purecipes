package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.shared.domain.model.MeasurementPreferences
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	operator fun invoke(): Flow<MeasurementPreferences> = repository.observeMeasurementPreferences()
}

@Inject
class GetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(): MeasurementPreferences = repository.getMeasurementPreferences()
}

@Inject
class SaveMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(preferences: MeasurementPreferences) {
		repository.saveMeasurementPreferences(preferences)
	}
}

@Inject
class ResetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke() {
		repository.resetMeasurementPreferences()
	}
}

@Inject
class MarkMeasurementMismatchSeenUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(recipeId: Int) {
		repository.markMismatchNotificationSeen(recipeId)
	}
}

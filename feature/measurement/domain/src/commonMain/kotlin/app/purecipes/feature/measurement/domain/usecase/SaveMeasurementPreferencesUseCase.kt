package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.shared.domain.model.MeasurementPreferences
import dev.zacsweers.metro.Inject

@Inject
class SaveMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(preferences: MeasurementPreferences) {
		repository.saveMeasurementPreferences(preferences)
	}
}

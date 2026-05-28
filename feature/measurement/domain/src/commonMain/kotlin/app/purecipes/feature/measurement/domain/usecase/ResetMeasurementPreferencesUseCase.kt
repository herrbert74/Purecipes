package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import dev.zacsweers.metro.Inject

@Inject
class ResetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke() {
		repository.resetMeasurementPreferences()
	}
}

package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.shared.domain.model.MeasurementPreferences
import dev.zacsweers.metro.Inject

@Inject
class GetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(): MeasurementPreferences = repository.getMeasurementPreferences()
}

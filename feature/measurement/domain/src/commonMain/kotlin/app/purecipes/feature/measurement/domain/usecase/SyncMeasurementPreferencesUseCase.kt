package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import dev.zacsweers.metro.Inject

@Inject
class SyncMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke() {
		repository.syncMeasurementPreferencesWithRemote()
	}
}

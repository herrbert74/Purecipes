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

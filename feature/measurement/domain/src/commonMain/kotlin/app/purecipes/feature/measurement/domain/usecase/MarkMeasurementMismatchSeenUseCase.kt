package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import dev.zacsweers.metro.Inject

@Inject
class MarkMeasurementMismatchSeenUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(recipeId: Int) {
		repository.markMismatchNotificationSeen(recipeId)
	}
}

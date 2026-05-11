package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMeasurementPreferencesRepository(
	private val defaults: MeasurementPreferences = MeasurementPreferences(
		preferredSystem = MeasurementSystem.METRIC,
	),
) : MeasurementPreferencesRepository {

	private val flow = MutableStateFlow(defaults)

	override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = flow

	override suspend fun getMeasurementPreferences(): MeasurementPreferences = flow.value

	override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
		flow.value = preferences
	}

	override suspend fun resetMeasurementPreferences() {
		flow.value = defaults
	}

	override suspend fun markMismatchNotificationSeen(recipeId: Int) = Unit
}

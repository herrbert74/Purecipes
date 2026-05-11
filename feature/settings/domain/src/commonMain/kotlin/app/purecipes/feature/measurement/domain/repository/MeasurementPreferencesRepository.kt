package app.purecipes.feature.measurement.domain.repository

import app.purecipes.shared.domain.model.MeasurementPreferences
import kotlinx.coroutines.flow.Flow

interface MeasurementPreferencesRepository {

	fun observeMeasurementPreferences(): Flow<MeasurementPreferences>

	suspend fun getMeasurementPreferences(): MeasurementPreferences

	suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences)

	suspend fun resetMeasurementPreferences()

	suspend fun markMismatchNotificationSeen(recipeId: Int)
}

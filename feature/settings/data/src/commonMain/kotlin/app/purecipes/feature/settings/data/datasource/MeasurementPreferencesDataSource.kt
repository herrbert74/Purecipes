package app.purecipes.feature.settings.data.datasource

import app.purecipes.shared.domain.model.MeasurementPreferences
import kotlinx.coroutines.flow.Flow

interface MeasurementPreferencesDataSource {

	fun observeMeasurementPreferences(): Flow<MeasurementPreferences>

	fun getMeasurementPreferences(): MeasurementPreferences

	fun saveMeasurementPreferences(preferences: MeasurementPreferences)

	fun resetMeasurementPreferences()

	fun markMismatchNotificationSeen(recipeId: Int)
}

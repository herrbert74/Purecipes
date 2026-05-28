package app.purecipes.feature.measurement.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.MeasurementPreferences
import kotlinx.coroutines.flow.Flow

interface MeasurementPreferencesDataSource {

	interface Local {

		fun observeMeasurementPreferences(): Flow<MeasurementPreferences>

		fun getMeasurementPreferences(): MeasurementPreferences

		fun saveMeasurementPreferences(preferences: MeasurementPreferences)

		fun resetMeasurementPreferences()

		fun markMismatchNotificationSeen(recipeId: Int)
	}

	interface Remote {

		suspend fun getMeasurementPreferences(): Outcome<MeasurementPreferences>

		suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences): Outcome<MeasurementPreferences>
	}
}

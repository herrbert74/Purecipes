package com.purecipes.feature.settings.data.repository

import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.feature.settings.data.datasource.MeasurementPreferencesDataSource
import com.purecipes.shared.domain.model.MeasurementPreferences
import kotlinx.coroutines.flow.Flow

class MeasurementPreferencesAccessor(
	private val localDataSource: MeasurementPreferencesDataSource,
) : MeasurementPreferencesRepository {

	override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> {
		return localDataSource.observeMeasurementPreferences()
	}

	override suspend fun getMeasurementPreferences(): MeasurementPreferences {
		return localDataSource.getMeasurementPreferences()
	}

	override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
		localDataSource.saveMeasurementPreferences(preferences)
	}

	override suspend fun resetMeasurementPreferences() {
		localDataSource.resetMeasurementPreferences()
	}

	override suspend fun markMismatchNotificationSeen(recipeId: Int) {
		localDataSource.markMismatchNotificationSeen(recipeId)
	}
}

package com.purecipes.feature.measurement.domain.usecase

import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.shared.domain.model.MeasurementPreferences
import kotlinx.coroutines.flow.Flow

class ObserveMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	operator fun invoke(): Flow<MeasurementPreferences> = repository.observeMeasurementPreferences()
}

class GetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(): MeasurementPreferences = repository.getMeasurementPreferences()
}

class SaveMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(preferences: MeasurementPreferences) {
		repository.saveMeasurementPreferences(preferences)
	}
}

class ResetMeasurementPreferencesUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke() {
		repository.resetMeasurementPreferences()
	}
}

class MarkMeasurementMismatchSeenUseCase(
	private val repository: MeasurementPreferencesRepository,
) {

	suspend operator fun invoke(recipeId: Int) {
		repository.markMismatchNotificationSeen(recipeId)
	}
}

package com.purecipes.feature.settings.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.MeasurementPreferences

interface MeasurementPreferencesRemoteDataSource {

	suspend fun getMeasurementPreferences(): Outcome<MeasurementPreferences>

	suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences): Outcome<MeasurementPreferences>
}

class PurecipesMeasurementPreferencesRemoteDataSource(
	private val api: PurecipesApi,
) : MeasurementPreferencesRemoteDataSource {

	override suspend fun getMeasurementPreferences(): Outcome<MeasurementPreferences> = runCatchingApi {
		api.getMeasurementPreferences()
	}

	override suspend fun saveMeasurementPreferences(
		preferences: MeasurementPreferences,
	): Outcome<MeasurementPreferences> = runCatchingApi {
		api.saveMeasurementPreferences(preferences)
	}
}

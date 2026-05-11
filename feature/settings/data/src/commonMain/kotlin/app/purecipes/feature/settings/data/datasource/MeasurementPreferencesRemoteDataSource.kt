package app.purecipes.feature.settings.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.MeasurementPreferences

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

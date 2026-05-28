package app.purecipes.feature.measurement.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.MeasurementPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class MeasurementPreferencesRemoteDataSource(
	private val api: PurecipesApi,
) : MeasurementPreferencesDataSource.Remote {

	override suspend fun getMeasurementPreferences(): Outcome<MeasurementPreferences> = runCatchingApi {
		api.getMeasurementPreferences()
	}

	override suspend fun saveMeasurementPreferences(
		preferences: MeasurementPreferences,
	): Outcome<MeasurementPreferences> = runCatchingApi {
		api.saveMeasurementPreferences(preferences)
	}
}

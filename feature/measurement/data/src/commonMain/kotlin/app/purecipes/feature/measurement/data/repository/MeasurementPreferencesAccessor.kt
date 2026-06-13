package app.purecipes.feature.measurement.data.repository

import app.purecipes.feature.measurement.data.datasource.MeasurementPreferencesDataSource
import app.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.domain.model.MeasurementPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Inject
@ContributesBinding(AppScope::class)
class MeasurementPreferencesAccessor(
	private val localDataSource: MeasurementPreferencesDataSource.Local,
	private val remoteDataSource: MeasurementPreferencesDataSource.Remote,
	private val sessionTokenStore: SessionTokenStore,
) : MeasurementPreferencesRepository {

	override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> {
		return localDataSource.observeMeasurementPreferences()
	}

	override suspend fun getMeasurementPreferences(): MeasurementPreferences {
		return localDataSource.getMeasurementPreferences()
	}

	override suspend fun syncMeasurementPreferencesWithRemote() {
		syncRemoteToLocalIfAuthenticated()
	}

	override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
		localDataSource.saveMeasurementPreferences(preferences)
		withContext(NonCancellable) {
			syncLocalToRemote(preferences)
		}
	}

	override suspend fun resetMeasurementPreferences() {
		localDataSource.resetMeasurementPreferences()
		withContext(NonCancellable) {
			syncLocalToRemote(localDataSource.getMeasurementPreferences())
		}
	}

	override suspend fun markMismatchNotificationSeen(recipeId: Int) {
		localDataSource.markMismatchNotificationSeen(recipeId)
		withContext(NonCancellable) {
			syncLocalToRemote(localDataSource.getMeasurementPreferences())
		}
	}

	private suspend fun syncRemoteToLocalIfAuthenticated() {
		if (!isAuthenticated()) {
			return
		}

		val result = remoteDataSource.getMeasurementPreferences()
		if (result.isOk) {
			result.component1()?.let(localDataSource::saveMeasurementPreferences)
		} else {
			syncLocalToRemote(localDataSource.getMeasurementPreferences())
		}
	}

	private suspend fun syncLocalToRemote(preferences: MeasurementPreferences) {
		if (!isAuthenticated()) {
			return
		}

		val result = remoteDataSource.saveMeasurementPreferences(preferences)
		if (result.isOk) {
			result.component1()?.let(localDataSource::saveMeasurementPreferences)
		}
	}

	private fun isAuthenticated(): Boolean {
		return !sessionTokenStore.currentAccessToken().isNullOrBlank()
	}
}

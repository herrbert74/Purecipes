package app.purecipes.feature.measurement.data.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.measurement.data.datasource.MeasurementPreferencesDataSource
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeFormatHandling
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MeasurementPreferencesAccessorTest {

	@Test
	fun `save completes remote sync even when caller is cancelled`() = runTest {
		val initialPreferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
		)
		val updatedPreferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.IMPERIAL,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
			detectedCountryCode = "US",
		)
		val session = AuthenticatedSession(
			accessToken = "session-token",
			expiresAtEpochSeconds = 4_000_000_000,
			user = AuthenticatedBackendUser(
				id = "1",
				email = "user@example.com",
				displayName = "User",
				firstName = "User",
				familyName = "Example",
				profileImageUrl = null,
				provider = "GOOGLE",
			),
		)
		val remoteDataSource = FakeMeasurementPreferencesRemoteDataSource(initialPreferences)
		val accessor = MeasurementPreferencesAccessor(
			localDataSource = FakeMeasurementPreferencesLocalDataSource(initialPreferences),
			remoteDataSource = remoteDataSource,
			sessionTokenStore = FakeSessionTokenStore(session),
		)

		val saveJob = launch(start = CoroutineStart.UNDISPATCHED) {
			accessor.saveMeasurementPreferences(updatedPreferences)
		}
		saveJob.cancel()
		advanceUntilIdle()

		remoteDataSource.savedPreferences.single() shouldBe updatedPreferences
	}

	private class FakeMeasurementPreferencesLocalDataSource(
		private val initialPreferences: MeasurementPreferences,
	) : MeasurementPreferencesDataSource.Local {

		private val preferencesFlow = MutableStateFlow(initialPreferences)

		override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = preferencesFlow

		override fun getMeasurementPreferences(): MeasurementPreferences = preferencesFlow.value

		override fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
			preferencesFlow.value = preferences
		}

		override fun resetMeasurementPreferences() {
			preferencesFlow.value = initialPreferences
		}

		override fun markMismatchNotificationSeen(recipeId: Int) {
			preferencesFlow.value = preferencesFlow.value.copy(
				notificationSeenRecipeIds = preferencesFlow.value.notificationSeenRecipeIds + recipeId,
			)
		}
	}

	private class FakeMeasurementPreferencesRemoteDataSource(
		private val initialPreferences: MeasurementPreferences,
	) : MeasurementPreferencesDataSource.Remote {

		val savedPreferences = mutableListOf<MeasurementPreferences>()

		override suspend fun getMeasurementPreferences(): Outcome<MeasurementPreferences> {
			return Ok(savedPreferences.lastOrNull() ?: initialPreferences)
		}

		override suspend fun saveMeasurementPreferences(
			preferences: MeasurementPreferences,
		): Outcome<MeasurementPreferences> {
			delay(1)
			savedPreferences += preferences
			return Ok(preferences)
		}
	}
}

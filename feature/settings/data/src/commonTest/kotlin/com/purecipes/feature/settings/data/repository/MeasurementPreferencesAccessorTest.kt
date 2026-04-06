package com.purecipes.feature.settings.data.repository

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.settings.data.datasource.MeasurementPreferencesDataSource
import com.purecipes.feature.settings.data.datasource.MeasurementPreferencesRemoteDataSource
import com.purecipes.shared.data.session.SessionTokenStore
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
		val remoteDataSource = FakeMeasurementPreferencesRemoteDataSource(initialPreferences)
		val accessor = MeasurementPreferencesAccessor(
			localDataSource = FakeMeasurementPreferencesDataSource(initialPreferences),
			remoteDataSource = remoteDataSource,
			sessionTokenStore = FakeSessionTokenStore(),
		)

		val saveJob = launch(start = CoroutineStart.UNDISPATCHED) {
			accessor.saveMeasurementPreferences(updatedPreferences)
		}
		saveJob.cancel()
		advanceUntilIdle()

		assertEquals(updatedPreferences, remoteDataSource.savedPreferences.single())
	}

	private class FakeMeasurementPreferencesDataSource(
		private val initialPreferences: MeasurementPreferences,
	) : MeasurementPreferencesDataSource {

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
	) : MeasurementPreferencesRemoteDataSource {

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

	private class FakeSessionTokenStore : SessionTokenStore {

		private val session = AuthenticatedSession(
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

		override fun currentSession(): AuthenticatedSession = session

		override fun currentAccessToken(): String = session.accessToken

		override fun saveSession(session: AuthenticatedSession) = Unit

		override fun clearSession() = Unit
	}
}

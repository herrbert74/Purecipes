package com.purecipes.feature.auth.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import com.purecipes.feature.auth.data.datasource.AuthenticationStore
import com.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.shared.data.session.SessionTokenStore
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthenticationAccessorTest {

	@Test
	fun `register signs the user in`() = runTest {
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)

		val user = accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		).get()

		assertEquals("Taylor Baker", user?.displayName)
		assertEquals(AuthProvider.EMAIL, user?.provider)
		assertIs<AuthenticationState.SignedIn>(accessor.authenticationState.value)
	}

	@Test
	fun `duplicate registration returns an error`() = runTest {
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)

		accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		)
		val result = accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		)

		assertEquals("An account already exists for this email", result.getError()?.message)
	}

	@Test
	fun `google sign in uses backend verified user`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), sessionTokenStore),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)

		val user = accessor.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = "verified-id-token",
				email = "taylor@example.com",
				displayName = "Ignored Client Name",
				profileImageUrl = "https://example.com/ignored-avatar.png",
			),
		).get()

		assertEquals(AuthProvider.GOOGLE, user?.provider)
		assertEquals("42", user?.id)
		assertEquals("Taylor Baker", user?.displayName)
		assertEquals("https://example.com/avatar.png", user?.profileImageUrl)
		assertEquals("session-token", sessionTokenStore.currentAccessToken())
		assertNull(user?.familyName?.takeIf { it.isBlank() })
	}

	@Test
	fun `google sign in returns backend verification error`() = runTest {
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(
				result = Err(Failure.ServerError("Token verification failed")),
			),
		)

		val result = accessor.signInWithGoogle(
			GoogleAuthenticationProfile(
				idToken = "bad-id-token",
				email = "taylor@example.com",
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		assertEquals("Token verification failed", result.getError()?.message)
		assertIs<AuthenticationState.SignedOut>(accessor.authenticationState.value)
	}

	private class FakeAuthenticationRemoteDataSource(
		private val result: Outcome<AuthenticatedSession> = Ok(
			AuthenticatedSession(
				accessToken = "session-token",
				expiresAtEpochSeconds = 4_000_000_000,
				user = AuthenticatedBackendUser(
					id = "42",
					email = "taylor@example.com",
					displayName = "Taylor Baker",
					firstName = "Taylor",
					familyName = "Baker",
					profileImageUrl = "https://example.com/avatar.png",
					provider = "GOOGLE",
				),
			),
		),
	) : AuthenticationDataSource.Remote {

		override suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession> = result

		override suspend fun getCurrentSession(): Outcome<AuthenticatedSession> = result

		override suspend fun signOut() = Unit
	}

	private class FakeSessionTokenStore : SessionTokenStore {

		private var session: AuthenticatedSession? = null

		override fun currentSession(): AuthenticatedSession? = session

		override fun currentAccessToken(): String? = session?.accessToken

		override fun saveSession(session: AuthenticatedSession) {
			this.session = session
		}

		override fun clearSession() {
			session = null
		}
	}
}

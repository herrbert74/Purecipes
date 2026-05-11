package app.purecipes.feature.auth.data.repository

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

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

		user?.displayName shouldBe "Taylor Baker"
		user?.provider shouldBe AuthProvider.EMAIL
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
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

		result.getError()?.message shouldBe "An account already exists for this email"
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

		user?.provider shouldBe AuthProvider.GOOGLE
		user?.id shouldBe "42"
		user?.displayName shouldBe "Taylor Baker"
		user?.profileImageUrl shouldBe "https://example.com/avatar.png"
		sessionTokenStore.currentAccessToken() shouldBe "session-token"
		user?.familyName?.takeIf { it.isBlank() } shouldBe null
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

		result.getError()?.message shouldBe "Token verification failed"
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
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
}

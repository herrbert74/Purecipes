package app.purecipes.feature.auth.data.repository

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.data.datasource.FakeFirebaseEmailPasswordAuth
import app.purecipes.feature.auth.data.datasource.FirebaseAuthenticationLocalDataSource
import app.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.FacebookAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
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
	fun `register creates account but does not sign in`() = runTest {
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)

		val result = accessor.registerWithEmail(
			displayName = "Taylor Baker",
			email = "taylor@example.com",
			password = "secret",
		)

		result.getError() shouldBe null
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
	}

	@Test
	fun `duplicate registration returns an error`() = runTest {
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)

		accessor.registerWithEmail(
			displayName = "Taylor Baker",
			email = "taylor@example.com",
			password = "secret",
		)
		val result = accessor.registerWithEmail(
			displayName = "Taylor Baker",
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
	fun `sign in with incorrect password returns user facing error`() = runTest {
		val store = AuthenticationStore()
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(store, FakeSessionTokenStore()),
			remoteDataSource = FakeAuthenticationRemoteDataSource(),
		)
		accessor.registerWithEmail(
			displayName = "Taylor Baker",
			email = "taylor@example.com",
			password = "secret",
		)

		val result = accessor.signInWithEmail("taylor@example.com", "wrong-password")

		result.getError()?.message shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
	}

	@Test
	fun `facebook sign in uses backend verified user`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(AuthenticationStore(), sessionTokenStore),
			remoteDataSource = FakeAuthenticationRemoteDataSource(
				result = Ok(
					AuthenticatedSession(
						accessToken = "session-token",
						expiresAtEpochSeconds = 4_000_000_000,
						user = AuthenticatedBackendUser(
							id = "43",
							email = "taylor@example.com",
							displayName = "Taylor Baker",
							firstName = "Taylor",
							familyName = "Baker",
							profileImageUrl = "https://example.com/avatar.png",
							provider = "FACEBOOK",
						),
					),
				),
			),
		)

		val user = accessor.signInWithFacebook(
			FacebookAuthenticationProfile(
				idToken = "verified-id-token",
				email = "taylor@example.com",
				displayName = "Ignored Client Name",
				profileImageUrl = "https://example.com/ignored-avatar.png",
			),
		).get()

		user?.provider shouldBe AuthProvider.FACEBOOK
		user?.id shouldBe "43"
		user?.displayName shouldBe "Taylor Baker"
		user?.profileImageUrl shouldBe "https://example.com/avatar.png"
		sessionTokenStore.currentAccessToken() shouldBe "session-token"
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

	@Test
	fun `delete account deletes the firebase identity before backend data and then signs out`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val firebaseAuthService = FakeFirebaseEmailPasswordAuth()
		val localDataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = firebaseAuthService,
		)
		var identityDeletionsBeforeBackendCall = 0
		var accessTokenDuringBackendCall: String? = null
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			onDeleteAccount = {
				identityDeletionsBeforeBackendCall = firebaseAuthService.deleteCurrentUserCallCount
				accessTokenDuringBackendCall = sessionTokenStore.currentAccessToken()
			},
		)
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		val result = accessor.deleteAccount()

		result.getError() shouldBe null
		identityDeletionsBeforeBackendCall shouldBe 1
		accessTokenDuringBackendCall shouldBe "session-token"
		remoteDataSource.deleteAccountCallCount shouldBe 1
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		sessionTokenStore.currentSession() shouldBe null
	}

	@Test
	fun `delete account keeps the session when firebase requires a recent login`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val localDataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(
				deleteCurrentUserHandler = { error("This operation requires recent authentication") },
			),
		)
		val remoteDataSource = FakeAuthenticationRemoteDataSource()
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		val result = accessor.deleteAccount()

		result.getError()?.message shouldBe "This operation requires recent authentication"
		remoteDataSource.deleteAccountCallCount shouldBe 0
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		sessionTokenStore.currentAccessToken() shouldBe "session-token"
	}

	@Test
	fun `delete account keeps the session when backend deletion fails so it can be retried`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val localDataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(),
		)
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			deleteAccountResults = listOf(Err(Failure.IoFailure), Ok(Unit)),
		)
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		val failedResult = accessor.deleteAccount()

		failedResult.getError() shouldBe Failure.IoFailure
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		sessionTokenStore.currentAccessToken() shouldBe "session-token"

		val retriedResult = accessor.deleteAccount()

		retriedResult.getError() shouldBe null
		remoteDataSource.deleteAccountCallCount shouldBe 2
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		sessionTokenStore.currentSession() shouldBe null
	}

	@Test
	fun `validateSession signs out when backend session is unauthorized`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val localDataSource = InMemoryAuthenticationLocalDataSource(
			AuthenticationStore(),
			sessionTokenStore,
		)
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			getCurrentSessionResult = Err(Failure.UserNotLoggedIn),
		)
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		accessor.validateSession()

		remoteDataSource.getCurrentSessionCallCount shouldBe 1
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		sessionTokenStore.currentSession() shouldBe null
	}

	@Test
	fun `validateSession keeps signed in state when backend session is valid`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val localDataSource = InMemoryAuthenticationLocalDataSource(
			AuthenticationStore(),
			sessionTokenStore,
		)
		val refreshedSession = fakeSession().copy(accessToken = "refreshed-session-token")
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			getCurrentSessionResult = Ok(refreshedSession),
		)
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		accessor.validateSession()

		remoteDataSource.getCurrentSessionCallCount shouldBe 1
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		sessionTokenStore.currentAccessToken() shouldBe "refreshed-session-token"
	}

	@Test
	fun `validateSession keeps signed in state when validation fails with network error`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val localDataSource = InMemoryAuthenticationLocalDataSource(
			AuthenticationStore(),
			sessionTokenStore,
		)
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			getCurrentSessionResult = Err(Failure.IoFailure),
		)
		val accessor = AuthenticationAccessor(localDataSource, remoteDataSource)
		localDataSource.signInWithBackendSession(fakeSession())

		accessor.validateSession()

		remoteDataSource.getCurrentSessionCallCount shouldBe 1
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		sessionTokenStore.currentAccessToken() shouldBe "session-token"
	}

	@Test
	fun `validateSession skips remote check when signed out`() = runTest {
		val remoteDataSource = FakeAuthenticationRemoteDataSource(
			getCurrentSessionResult = Err(Failure.UserNotLoggedIn),
		)
		val accessor = AuthenticationAccessor(
			localDataSource = InMemoryAuthenticationLocalDataSource(
				AuthenticationStore(),
				FakeSessionTokenStore(),
			),
			remoteDataSource = remoteDataSource,
		)

		accessor.validateSession()

		remoteDataSource.getCurrentSessionCallCount shouldBe 0
		accessor.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
	}

	private fun fakeSession(): AuthenticatedSession = AuthenticatedSession(
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
	)

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
		private val getCurrentSessionResult: Outcome<AuthenticatedSession> = result,
		private val deleteAccountResults: List<Outcome<Unit>> = listOf(Ok(Unit)),
		private val onDeleteAccount: () -> Unit = {},
	) : AuthenticationDataSource.Remote {

		var deleteAccountCallCount = 0
			private set

		var getCurrentSessionCallCount = 0
			private set

		override suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession> = result

		override suspend fun signInWithFacebook(idToken: String): Outcome<AuthenticatedSession> = result

		override suspend fun signInWithEmailToken(idToken: String): Outcome<AuthenticatedSession> = result

		override suspend fun getCurrentSession(): Outcome<AuthenticatedSession> {
			getCurrentSessionCallCount++
			return getCurrentSessionResult
		}

		override suspend fun deleteAccount(): Outcome<Unit> {
			onDeleteAccount()
			val deleteAccountResult = deleteAccountResults.getOrElse(deleteAccountCallCount) { Ok(Unit) }
			deleteAccountCallCount++
			return deleteAccountResult
		}

		override suspend fun signOut() = Unit
	}
}

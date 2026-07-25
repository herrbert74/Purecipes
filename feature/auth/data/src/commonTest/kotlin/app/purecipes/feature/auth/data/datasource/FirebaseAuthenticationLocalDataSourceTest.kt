package app.purecipes.feature.auth.data.datasource

import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FirebaseAuthenticationLocalDataSourceTest {

	@Test
	fun `sign in with invalid credentials returns user facing error`() = runTest {
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = FakeSessionTokenStore(),
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(
				signInHandler = { _, _ ->
					EmailPasswordSignInResult(errorMessage = INCORRECT_EMAIL_OR_PASSWORD_MESSAGE)
				},
			),
		)

		val result = dataSource.signInWithEmail("taylor@example.com", "wrong-password")

		result.getError()?.message shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		dataSource.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
	}

	@Test
	fun `register passes display name to firebase`() = runTest {
		val firebaseAuthService = FakeFirebaseEmailPasswordAuth()
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = FakeSessionTokenStore(),
			firebaseAuthService = firebaseAuthService,
		)

		val result = dataSource.registerWithEmail(
			displayName = "Taylor Baker",
			email = "taylor@example.com",
			password = "secret",
		)

		result.getError() shouldBe null
		firebaseAuthService.lastRegisteredDisplayName shouldBe "Taylor Baker"
	}

	@Test
	fun `resend verification with invalid credentials returns user facing error`() = runTest {
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = FakeSessionTokenStore(),
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(
				resendHandler = { _, _ ->
					EmailPasswordSignInResult(errorMessage = INCORRECT_EMAIL_OR_PASSWORD_MESSAGE)
				},
			),
		)

		val result = dataSource.resendEmailVerification("taylor@example.com", "wrong-password")

		result.getError()?.message shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
	}

	@Test
	fun `send password reset email calls firebase with normalized email`() = runTest {
		val firebaseAuthService = FakeFirebaseEmailPasswordAuth()
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = FakeSessionTokenStore(),
			firebaseAuthService = firebaseAuthService,
		)

		val result = dataSource.sendPasswordResetEmail("  Taylor@Example.com  ")

		result.getError() shouldBe null
		firebaseAuthService.lastPasswordResetEmail shouldBe "taylor@example.com"
	}

	@Test
	fun `delete authentication identity keeps the backend session for the following backend call`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val firebaseAuthService = FakeFirebaseEmailPasswordAuth()
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = firebaseAuthService,
		)
		dataSource.signInWithBackendSession(fakeAuthenticatedSession())

		val result = dataSource.deleteAuthenticationIdentity()

		result.getError() shouldBe null
		firebaseAuthService.deleteCurrentUserCallCount shouldBe 1
		dataSource.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
		sessionTokenStore.currentAccessToken() shouldBe "session-token"
	}

	@Test
	fun `delete authentication identity reports firebase recent login failure`() = runTest {
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = FakeSessionTokenStore(),
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(
				deleteCurrentUserHandler = { error("This operation requires recent authentication") },
			),
		)
		dataSource.signInWithBackendSession(fakeAuthenticatedSession())

		val result = dataSource.deleteAuthenticationIdentity()

		result.getError()?.message shouldBe "This operation requires recent authentication"
		dataSource.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedIn>()
	}

	@Test
	fun `sign out clears signed in state`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(),
		)
		dataSource.signInWithExternalProvider(fakeAuthUser(id = "firebase-user"))

		dataSource.signOut()

		dataSource.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		sessionTokenStore.currentSession() shouldBe null
	}

	private fun fakeAuthenticatedSession() = AuthenticatedSession(
		accessToken = "session-token",
		expiresAtEpochSeconds = 4_000_000_000,
		user = AuthenticatedBackendUser(
			id = "42",
			email = "taylor@example.com",
			displayName = "Taylor Baker",
			firstName = "Taylor",
			familyName = "Baker",
			profileImageUrl = null,
			provider = "GOOGLE",
		),
	)
}

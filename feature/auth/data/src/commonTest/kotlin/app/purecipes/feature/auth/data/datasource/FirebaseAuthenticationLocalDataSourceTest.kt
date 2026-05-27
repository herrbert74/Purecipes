package app.purecipes.feature.auth.data.datasource

import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
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
	fun `delete account clears signed in state`() = runTest {
		val sessionTokenStore = FakeSessionTokenStore()
		val dataSource = FirebaseAuthenticationLocalDataSource(
			store = AuthenticationStore(),
			sessionTokenStore = sessionTokenStore,
			firebaseAuthService = FakeFirebaseEmailPasswordAuth(),
		)
		val user = fakeAuthUser(id = "firebase-user")
		dataSource.signInWithExternalProvider(user)

		val result = dataSource.deleteAccount()

		result.getError() shouldBe null
		dataSource.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		sessionTokenStore.currentSession() shouldBe null
	}
}

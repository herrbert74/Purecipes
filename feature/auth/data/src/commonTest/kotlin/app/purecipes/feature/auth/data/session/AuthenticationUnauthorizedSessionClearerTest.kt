package app.purecipes.feature.auth.data.session

import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.datatestfixtures.fake.FakeSessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class AuthenticationUnauthorizedSessionClearerTest {

	@Test
	fun `clears session token and signs the user out`() {
		val sessionTokenStore = FakeSessionTokenStore(
			AuthenticatedSession(
				accessToken = "session-token",
				expiresAtEpochSeconds = 4_000_000_000,
				user = AuthenticatedBackendUser(
					id = "42",
					email = "taylor@example.com",
					displayName = "Taylor Baker",
					provider = "GOOGLE",
				),
			),
		)
		val store = AuthenticationStore(
			AuthenticationState.SignedIn(
				fakeAuthUser(provider = AuthProvider.GOOGLE),
			),
		)
		val clearer = AuthenticationUnauthorizedSessionClearer(
			sessionTokenStore = sessionTokenStore,
			store = store,
		)

		clearer.clearUnauthorizedSession()

		sessionTokenStore.currentSession().shouldBeNull()
		store.authenticationState.value.shouldBeInstanceOf<AuthenticationState.SignedOut>()
		store.authenticationState.value shouldBe AuthenticationState.SignedOut
	}
}

package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.fakeAuthUser
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ValidateSessionUseCaseTest {

	@Test
	fun `delegates validation to the repository`() = runTest {
		val repository = FakeAuthenticationRepository(
			initialState = AuthenticationState.SignedIn(
				fakeAuthUser(provider = AuthProvider.GOOGLE),
			),
			signOutOnValidateSession = true,
		)

		ValidateSessionUseCase(repository)()

		repository.validateSessionCallCount shouldBe 1
		repository.authenticationState.value shouldBe AuthenticationState.SignedOut
	}
}

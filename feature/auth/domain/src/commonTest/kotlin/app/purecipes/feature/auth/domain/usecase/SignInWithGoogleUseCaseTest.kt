package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SignInWithGoogleUseCaseTest {

	@Test
	fun `google sign in rejects missing id token`() = runTest {
		val useCase = SignInWithGoogleUseCase(FakeAuthenticationRepository())

		val result = useCase(
			GoogleAuthenticationProfile(
				idToken = "",
				email = null,
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		result.getError()?.message shouldBe "Google sign-in did not return an ID token"
	}
}

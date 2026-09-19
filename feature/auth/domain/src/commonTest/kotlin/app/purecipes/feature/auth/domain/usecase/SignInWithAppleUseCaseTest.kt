package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.AppleAuthenticationProfile
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SignInWithAppleUseCaseTest {

	@Test
	fun `apple sign in rejects missing id token`() = runTest {
		val useCase = SignInWithAppleUseCase(FakeAuthenticationRepository())

		val result = useCase(
			AppleAuthenticationProfile(
				idToken = "",
				email = null,
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		result.getError()?.message shouldBe "Apple sign-in did not return an ID token"
	}

	@Test
	fun `apple sign in accepts missing email`() = runTest {
		val useCase = SignInWithAppleUseCase(FakeAuthenticationRepository())

		val result = useCase(
			AppleAuthenticationProfile(
				idToken = "token",
				email = null,
				displayName = "",
				profileImageUrl = null,
			),
		)

		result.get()?.provider shouldBe AuthProvider.APPLE
	}
}

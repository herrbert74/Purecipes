package app.purecipes.feature.auth.domain.usecase

import com.github.michaelbull.result.getError
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AuthenticationUseCasesTest {

	@Test
	fun `register rejects blank first name`() = runTest {
		val useCase = RegisterWithEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(
			firstName = "",
			familyName = "Baker",
			email = "user@example.com",
			password = "secret",
		)

		result.getError()?.message shouldBe "First name is required"
	}

	@Test
	fun `register rejects blank family name`() = runTest {
		val useCase = RegisterWithEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(
			firstName = "Taylor",
			familyName = "",
			email = "user@example.com",
			password = "secret",
		)

		result.getError()?.message shouldBe "Family name is required"
	}

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

	@Test
	fun `external provider sign in rejects missing email`() = runTest {
		val useCase = SignInWithExternalProviderUseCase(FakeAuthenticationRepository())

		val result = useCase(
			ExternalAuthenticationProfile(
				provider = AuthProvider.APPLE,
				id = "apple-user",
				email = null,
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		result.getError()?.message shouldBe "Apple sign-in did not return an email address"
	}

}

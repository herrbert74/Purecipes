package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SignInWithExternalProviderUseCaseTest {

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

package app.purecipes.feature.auth.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RegisterWithEmailUseCaseTest {

	@Test
	fun `register rejects blank display name`() = runTest {
		val useCase = RegisterWithEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(
			displayName = "",
			email = "user@example.com",
			password = "secret",
		)

		result.getError()?.message shouldBe "Display name is required"
	}
}

package app.purecipes.feature.auth.domain.usecase

import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RegisterWithEmailUseCaseTest {

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
}

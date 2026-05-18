package app.purecipes.feature.auth.domain.usecase

import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SendPasswordResetEmailUseCaseTest {

	@Test
	fun `send password reset rejects blank email`() = runTest {
		val useCase = SendPasswordResetEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(email = "")

		result.getError()?.message shouldBe EMAIL_REQUIRED_MESSAGE
	}

	@Test
	fun `send password reset rejects invalid email`() = runTest {
		val useCase = SendPasswordResetEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(email = "not-an-email")

		result.getError()?.message shouldBe INVALID_EMAIL_MESSAGE
	}

	@Test
	fun `send password reset trims email before repository call`() = runTest {
		var requestedEmail: String? = null
		val repository = FakeAuthenticationRepository(
			sendPasswordResetEmailHandler = { email ->
				requestedEmail = email
				com.github.michaelbull.result.Ok(Unit)
			},
		)
		val useCase = SendPasswordResetEmailUseCase(repository)

		useCase(email = "  taylor@example.com  ")

		requestedEmail shouldBe "taylor@example.com"
	}
}

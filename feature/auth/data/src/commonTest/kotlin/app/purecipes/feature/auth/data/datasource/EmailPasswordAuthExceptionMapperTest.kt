package app.purecipes.feature.auth.data.datasource

import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EmailPasswordAuthExceptionMapperTest {

	@Test
	fun `invalid credentials exception maps to user message`() {
		val exception = InvalidCredentialsException(
			"The supplied auth credential is incorrect, malformed or has expired.",
		)

		mapEmailPasswordAuthException(exception) shouldBe INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
	}

	@Test
	fun `unknown exception keeps message`() {
		mapEmailPasswordAuthException(IllegalStateException("Service unavailable")) shouldBe "Service unavailable"
	}

	private class InvalidCredentialsException(
		override val message: String?,
	) : Exception(message)
}

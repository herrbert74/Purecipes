package app.purecipes.feature.auth.ui

import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_UPPERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PasswordPolicyValidationTest {

	@Test
	fun `blank password is required`() {
		validatePasswordPolicy("") shouldBe PASSWORD_REQUIRED_MESSAGE
	}

	@Test
	fun `short password is rejected`() {
		validatePasswordPolicy("Aa1bcdefg") shouldBe PASSWORD_TOO_SHORT_MESSAGE
	}

	@Test
	fun `password without uppercase is rejected`() {
		validatePasswordPolicy("validpass12") shouldBe PASSWORD_MISSING_UPPERCASE_MESSAGE
	}

	@Test
	fun `password without lowercase is rejected`() {
		validatePasswordPolicy("VALIDPASS12") shouldBe PASSWORD_MISSING_LOWERCASE_MESSAGE
	}

	@Test
	fun `password without number is rejected`() {
		validatePasswordPolicy("ValidPasswd") shouldBe PASSWORD_MISSING_NUMBER_MESSAGE
	}

	@Test
	fun `valid password is accepted`() {
		validatePasswordPolicy("ValidPass12") shouldBe null
	}
}

package app.purecipes.feature.auth.ui.authentication

import app.purecipes.shared.domain.model.PASSWORD_MISSING_LOWERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_NUMBER_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_MISSING_UPPERCASE_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_TOO_SHORT_MESSAGE

private const val MIN_PASSWORD_LENGTH = 10

internal fun validatePasswordPolicy(password: String): String? {
	return when {
		password.isBlank() -> PASSWORD_REQUIRED_MESSAGE
		password.length < MIN_PASSWORD_LENGTH -> PASSWORD_TOO_SHORT_MESSAGE
		password.none { it.isUpperCase() } -> PASSWORD_MISSING_UPPERCASE_MESSAGE
		password.none { it.isLowerCase() } -> PASSWORD_MISSING_LOWERCASE_MESSAGE
		password.none { it.isDigit() } -> PASSWORD_MISSING_NUMBER_MESSAGE
		else -> null
	}
}

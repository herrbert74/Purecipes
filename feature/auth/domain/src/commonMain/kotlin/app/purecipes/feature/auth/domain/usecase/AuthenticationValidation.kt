package app.purecipes.feature.auth.domain.usecase

import app.purecipes.shared.domain.model.EMAIL_REQUIRED_MESSAGE
import app.purecipes.shared.domain.model.INVALID_EMAIL_MESSAGE
import app.purecipes.shared.domain.model.PASSWORD_REQUIRED_MESSAGE

internal fun validateEmailCredentials(email: String, password: String): String? {
	return when {
		email.isBlank() -> EMAIL_REQUIRED_MESSAGE
		!email.contains('@') -> INVALID_EMAIL_MESSAGE
		password.isBlank() -> PASSWORD_REQUIRED_MESSAGE
		else -> null
	}
}

internal fun validateRegistration(
	firstName: String,
	familyName: String,
	email: String,
	password: String,
): String? {
	return when {
		firstName.isBlank() -> "First name is required"
		familyName.isBlank() -> "Family name is required"
		else -> validateEmailCredentials(email = email, password = password)
	}
}

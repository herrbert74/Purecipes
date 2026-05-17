package app.purecipes.feature.auth.domain.usecase

internal fun validateEmailCredentials(email: String, password: String): String? {
	return when {
		email.isBlank() -> "Email is required"
		!email.contains('@') -> "Enter a valid email address"
		password.isBlank() -> "Password is required"
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

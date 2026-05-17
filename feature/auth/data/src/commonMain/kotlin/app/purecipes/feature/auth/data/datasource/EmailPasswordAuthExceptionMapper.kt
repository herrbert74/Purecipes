package app.purecipes.feature.auth.data.datasource

import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE

internal fun mapEmailPasswordAuthException(throwable: Throwable): String {
	return if (throwable.isInvalidEmailPasswordCredentials()) {
		INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
	} else {
		throwable.message?.takeIf { it.isNotBlank() } ?: "Sign in failed"
	}
}

private fun Throwable.isInvalidEmailPasswordCredentials(): Boolean {
	if (this::class.simpleName.orEmpty().contains("InvalidCredentials", ignoreCase = true)) {
		return true
	}
	val message = message.orEmpty()
	return message.contains("supplied auth credential", ignoreCase = true) ||
		message.contains("wrong password", ignoreCase = true) ||
		message.contains("no user record", ignoreCase = true) ||
		message.contains("password is invalid", ignoreCase = true) ||
		message.contains("invalid login credentials", ignoreCase = true)
}

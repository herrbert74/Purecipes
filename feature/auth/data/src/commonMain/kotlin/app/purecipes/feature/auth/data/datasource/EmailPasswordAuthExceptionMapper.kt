package app.purecipes.feature.auth.data.datasource

import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE

internal fun mapEmailPasswordAuthException(throwable: Throwable): String {
	return when {
		throwable.isInvalidEmailPasswordCredentials() -> INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
		throwable.requiresRecentLogin() -> "Please sign in again before deleting your account."
		else -> throwable.message?.takeIf { it.isNotBlank() } ?: "Authentication failed"
	}
}

private fun Throwable.requiresRecentLogin(): Boolean {
	if (this::class.simpleName.orEmpty().contains("RecentLoginRequired", ignoreCase = true)) {
		return true
	}
	val message = message.orEmpty()
	return message.contains("requires-recent-login", ignoreCase = true) ||
		message.contains("recent login", ignoreCase = true)
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

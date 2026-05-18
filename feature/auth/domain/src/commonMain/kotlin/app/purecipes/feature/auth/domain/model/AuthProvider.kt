package app.purecipes.feature.auth.domain.model

enum class AuthProvider {
	EMAIL,
	GOOGLE,
	APPLE,
	FACEBOOK,
}

internal fun String.toAuthProvider(): AuthProvider {
	return when (uppercase()) {
		"EMAIL" -> AuthProvider.EMAIL
		"GOOGLE" -> AuthProvider.GOOGLE
		"APPLE" -> AuthProvider.APPLE
		"FACEBOOK" -> AuthProvider.FACEBOOK
		else -> AuthProvider.EMAIL
	}
}

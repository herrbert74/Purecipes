package app.purecipes.feature.auth.domain.model

enum class AuthProvider {
	EMAIL,
	GOOGLE,
	APPLE,
	FACEBOOK,
}

data class AuthUser(
	val id: String,
	val email: String,
	val displayName: String,
	val firstName: String?,
	val familyName: String?,
	val profileImageUrl: String?,
	val provider: AuthProvider,
)

sealed interface AuthenticationState {
	data object SignedOut : AuthenticationState

	data class SignedIn(val user: AuthUser) : AuthenticationState
}

data class GoogleAuthenticationProfile(
	val idToken: String,
	val email: String?,
	val displayName: String,
	val profileImageUrl: String?,
)

data class ExternalAuthenticationProfile(
	val provider: AuthProvider,
	val id: String,
	val email: String?,
	val displayName: String?,
	val profileImageUrl: String?,
)

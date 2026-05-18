package app.purecipes.feature.auth.domain.model

data class GoogleAuthenticationProfile(
	val idToken: String,
	val email: String?,
	val displayName: String,
	val profileImageUrl: String?,
)

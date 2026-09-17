package app.purecipes.feature.auth.domain.model

data class AppleAuthenticationProfile(
	val idToken: String,
	val email: String?,
	val displayName: String,
	val profileImageUrl: String?,
)

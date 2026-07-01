package app.purecipes.feature.auth.domain.model

data class FacebookAuthenticationProfile(
	val idToken: String,
	val email: String?,
	val displayName: String,
	val profileImageUrl: String?,
)

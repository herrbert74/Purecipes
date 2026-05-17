package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleSignInRequest(
	val idToken: String,
)

@Serializable
data class EmailSignInRequest(
	val idToken: String,
)

@Serializable
data class VerifiedGoogleUser(
	val id: String,
	val email: String,
	val displayName: String,
	val firstName: String? = null,
	val familyName: String? = null,
	val profileImageUrl: String? = null,
)

@Serializable
data class AuthenticatedBackendUser(
	val id: String,
	val email: String,
	val displayName: String,
	val firstName: String? = null,
	val familyName: String? = null,
	val profileImageUrl: String? = null,
	val provider: String,
)

@Serializable
data class AuthenticatedSession(
	val accessToken: String,
	val expiresAtEpochSeconds: Long,
	val user: AuthenticatedBackendUser,
)

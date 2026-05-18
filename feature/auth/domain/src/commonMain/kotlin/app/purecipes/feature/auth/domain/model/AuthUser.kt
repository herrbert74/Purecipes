package app.purecipes.feature.auth.domain.model

import app.purecipes.shared.domain.model.AuthenticatedBackendUser

data class AuthUser(
	val id: String,
	val email: String,
	val displayName: String,
	val firstName: String?,
	val familyName: String?,
	val profileImageUrl: String?,
	val provider: AuthProvider,
)

fun AuthenticatedBackendUser.toAuthUser(): AuthUser {
	return AuthUser(
		id = id,
		email = email.trim().lowercase(),
		displayName = displayName,
		firstName = firstName,
		familyName = familyName,
		profileImageUrl = profileImageUrl,
		provider = provider.toAuthProvider(),
	)
}

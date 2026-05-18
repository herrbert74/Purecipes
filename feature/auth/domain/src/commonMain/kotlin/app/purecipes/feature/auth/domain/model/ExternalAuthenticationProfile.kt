package app.purecipes.feature.auth.domain.model

data class ExternalAuthenticationProfile(
	val provider: AuthProvider,
	val id: String,
	val email: String?,
	val displayName: String?,
	val profileImageUrl: String?,
)

fun ExternalAuthenticationProfile.toAuthUser(): AuthUser {
	val normalizedEmail = email?.trim()?.lowercase().orEmpty()
	val resolvedDisplayName = displayName?.trim().takeUnless { it.isNullOrBlank() }
		?: normalizedEmail.substringBefore('@').replaceFirstChar {
			if (it.isLowerCase()) it.titlecase() else it.toString()
		}
	return AuthUser(
		id = id.trim(),
		email = normalizedEmail,
		displayName = resolvedDisplayName,
		firstName = null,
		familyName = null,
		profileImageUrl = profileImageUrl,
		provider = provider,
	)
}

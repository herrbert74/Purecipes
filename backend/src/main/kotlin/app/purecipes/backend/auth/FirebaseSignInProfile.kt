package app.purecipes.backend.auth

internal const val APPLE_SIGN_IN_PROVIDER = "apple.com"
internal const val APPLE_FALLBACK_DISPLAY_NAME = "Apple user"
internal const val APPLE_FALLBACK_EMAIL_DOMAIN = "appleid.purecipes.app"
internal const val APPLE_PRIVATE_RELAY_HOST = "privaterelay.appleid.com"

internal fun resolvedFirebaseUserEmail(
	signInProvider: String?,
	email: String?,
	subject: String,
): String? {
	val normalizedEmail = email?.trim()?.lowercase()?.takeIf { it.isNotBlank() }
	val normalizedSubject = subject.trim()
	return if (normalizedEmail != null) {
		normalizedEmail
	} else if (signInProvider == APPLE_SIGN_IN_PROVIDER && normalizedSubject.isNotBlank()) {
		appleFallbackEmail(normalizedSubject)
	} else {
		null
	}
}

internal fun resolvedFirebaseUserDisplayName(
	signInProvider: String?,
	name: String?,
	email: String,
): String {
	val normalizedName = name?.trim()?.takeIf { it.isNotBlank() }
	return if (normalizedName != null) {
		normalizedName
	} else if (signInProvider == APPLE_SIGN_IN_PROVIDER && isApplePlaceholderEmail(email)) {
		APPLE_FALLBACK_DISPLAY_NAME
	} else {
		email.fallbackDisplayName()
	}
}

internal fun appleFallbackEmail(subject: String): String {
	return "apple.${subject.trim().lowercase()}@$APPLE_FALLBACK_EMAIL_DOMAIN"
}

internal fun isAppleFallbackEmail(email: String, subject: String): Boolean {
	return email.equals(appleFallbackEmail(subject), ignoreCase = true)
}

internal fun isApplePlaceholderEmail(email: String): Boolean {
	val host = email.substringAfter('@', missingDelimiterValue = "")
	return host.equals(APPLE_PRIVATE_RELAY_HOST, ignoreCase = true) ||
		host.equals(APPLE_FALLBACK_EMAIL_DOMAIN, ignoreCase = true)
}

internal fun String.fallbackDisplayName(): String {
	return substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

internal data class SessionUserProfile(
	val email: String,
	val displayName: String,
	val firstName: String?,
	val familyName: String?,
	val profileImageUrl: String?,
)

internal fun SessionUserProfile.mergedWithExisting(
	externalUserId: String,
	existing: SessionUserProfile,
): SessionUserProfile {
	val preservedEmail = if (
		isAppleFallbackEmail(email, externalUserId) &&
		existing.email.isNotBlank() &&
		!isAppleFallbackEmail(existing.email, externalUserId)
	) {
		existing.email
	} else {
		email
	}
	val preservedDisplayName = if (
		displayName == APPLE_FALLBACK_DISPLAY_NAME &&
		existing.displayName.isNotBlank() &&
		existing.displayName != APPLE_FALLBACK_DISPLAY_NAME
	) {
		existing.displayName
	} else {
		displayName
	}
	return copy(
		email = preservedEmail,
		displayName = preservedDisplayName,
		firstName = firstName ?: existing.firstName?.trim()?.takeIf { it.isNotBlank() },
		familyName = familyName ?: existing.familyName?.trim()?.takeIf { it.isNotBlank() },
		profileImageUrl = profileImageUrl ?: existing.profileImageUrl?.trim()?.takeIf { it.isNotBlank() },
	)
}

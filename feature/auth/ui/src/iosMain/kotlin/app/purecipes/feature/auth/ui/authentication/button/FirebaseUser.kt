package app.purecipes.feature.auth.ui.authentication.button

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import dev.gitlive.firebase.auth.FirebaseUser

internal suspend fun FirebaseUser.toGoogleAuthenticationProfile(): GoogleAuthenticationProfile? {
	val idToken = getIdToken(forceRefresh = true) ?: return null
	return GoogleAuthenticationProfile(
		idToken = idToken,
		email = email,
		displayName = displayName.orEmpty(),
		profileImageUrl = photoURL,
	)
}

internal fun FirebaseUser.toExternalAuthenticationProfile(provider: AuthProvider): ExternalAuthenticationProfile {
	return ExternalAuthenticationProfile(
		provider = provider,
		id = uid,
		email = email,
		displayName = displayName,
		profileImageUrl = photoURL,
	)
}

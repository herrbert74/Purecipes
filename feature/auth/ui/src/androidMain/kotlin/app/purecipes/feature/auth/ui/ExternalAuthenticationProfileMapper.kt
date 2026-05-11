package app.purecipes.feature.auth.ui

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import dev.gitlive.firebase.auth.FirebaseUser

internal fun FirebaseUser.toExternalAuthenticationProfile(provider: AuthProvider): ExternalAuthenticationProfile {
	return ExternalAuthenticationProfile(
		provider = provider,
		id = uid,
		email = email,
		displayName = displayName,
		profileImageUrl = photoURL,
	)
}

package com.purecipes.feature.auth.ui

import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
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

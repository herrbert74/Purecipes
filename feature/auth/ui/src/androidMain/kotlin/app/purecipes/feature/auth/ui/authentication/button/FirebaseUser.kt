package app.purecipes.feature.auth.ui.authentication.button

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.FacebookAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.mmk.kmpauth.core.auth.KMPAuthUser
import dev.gitlive.firebase.auth.FirebaseUser

internal suspend fun Result<KMPAuthUser>.toGoogleAuthenticationProfileResult(): Result<GoogleAuthenticationProfile?> {
	return fold(
		onSuccess = { user ->
			val firebaseUser = user.raw as? FirebaseUser
				?: return@fold Result.failure(
					IllegalStateException("Google sign-in did not return a Firebase user."),
				)
			Result.success(firebaseUser.toGoogleAuthenticationProfile())
		},
		onFailure = { error -> Result.failure(error) },
	)
}

internal suspend fun Result<KMPAuthUser>.toFacebookAuthenticationProfileResult():
	Result<FacebookAuthenticationProfile?> {
	return fold(
		onSuccess = { user ->
			val firebaseUser = user.raw as? FirebaseUser
				?: return@fold Result.failure(
					IllegalStateException("Facebook sign-in did not return a Firebase user."),
				)
			Result.success(firebaseUser.toFacebookAuthenticationProfile())
		},
		onFailure = { error -> Result.failure(error) },
	)
}

internal fun KMPAuthUser.toExternalAuthenticationProfile(provider: AuthProvider): ExternalAuthenticationProfile {
	return ExternalAuthenticationProfile(
		provider = provider,
		id = uid,
		email = email,
		displayName = displayName,
		profileImageUrl = photoUrl,
	)
}

internal suspend fun FirebaseUser.toGoogleAuthenticationProfile(): GoogleAuthenticationProfile? {
	val idToken = getIdToken(forceRefresh = true) ?: return null
	return GoogleAuthenticationProfile(
		idToken = idToken,
		email = email,
		displayName = displayName.orEmpty(),
		profileImageUrl = photoURL,
	)
}

internal suspend fun FirebaseUser.toFacebookAuthenticationProfile(): FacebookAuthenticationProfile? {
	val idToken = getIdToken(forceRefresh = true) ?: return null
	return FacebookAuthenticationProfile(
		idToken = idToken,
		email = email,
		displayName = displayName.orEmpty(),
		profileImageUrl = photoURL,
	)
}

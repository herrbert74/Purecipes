package app.purecipes.feature.auth.data.datasource

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

actual class FirebaseAuthService : FirebaseEmailPasswordAuth {
	actual override suspend fun signInWithEmailAndPassword(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		return runCatching {
			val result = Firebase.auth.signInWithEmailAndPassword(email, password)
			val user = result.user ?: return@runCatching EmailPasswordSignInResult()
			user.reload()
			if (!user.isEmailVerified) {
				return@runCatching EmailPasswordSignInResult(emailNotVerified = true)
			}
			EmailPasswordSignInResult(idToken = user.getIdToken(forceRefresh = true))
		}.getOrElse { EmailPasswordSignInResult(errorMessage = mapEmailPasswordAuthException(it)) }
	}

	actual override suspend fun createUserWithEmailAndPassword(
		email: String,
		password: String,
		displayName: String,
	) {
		val result = Firebase.auth.createUserWithEmailAndPassword(email, password)
		val user = result.user ?: error("Firebase user was not created")
		user.updateProfile(displayName = displayName)
	}

	actual override suspend fun sendEmailVerification() {
		Firebase.auth.currentUser?.sendEmailVerification()
	}

	actual override suspend fun resendEmailVerification(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		return runCatching {
			val result = Firebase.auth.signInWithEmailAndPassword(email, password)
			result.user?.sendEmailVerification()
			EmailPasswordSignInResult()
		}.getOrElse { EmailPasswordSignInResult(errorMessage = mapEmailPasswordAuthException(it)) }
	}

	actual override suspend fun sendPasswordResetEmail(email: String) {
		Firebase.auth.sendPasswordResetEmail(email)
	}

	actual override suspend fun signOut() {
		Firebase.auth.signOut()
	}
}

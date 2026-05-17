package app.purecipes.feature.auth.data.datasource

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

actual class FirebaseAuthService : FirebaseEmailPasswordAuth {
	actual override suspend fun signInWithEmailAndPassword(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		return try {
			val result = Firebase.auth.signInWithEmailAndPassword(email, password)
			val user = result.user ?: return EmailPasswordSignInResult()
			user.reload()
			if (!user.isEmailVerified) {
				return EmailPasswordSignInResult(emailNotVerified = true)
			}
			EmailPasswordSignInResult(idToken = user.getIdToken(forceRefresh = true))
		} catch (e: Exception) {
			EmailPasswordSignInResult(errorMessage = mapEmailPasswordAuthException(e))
		}
	}

	actual override suspend fun createUserWithEmailAndPassword(email: String, password: String) {
		Firebase.auth.createUserWithEmailAndPassword(email, password)
	}

	actual override suspend fun sendEmailVerification() {
		Firebase.auth.currentUser?.sendEmailVerification()
	}

	actual override suspend fun resendEmailVerification(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		return try {
			val result = Firebase.auth.signInWithEmailAndPassword(email, password)
			result.user?.sendEmailVerification()
			EmailPasswordSignInResult()
		} catch (e: Exception) {
			EmailPasswordSignInResult(errorMessage = mapEmailPasswordAuthException(e))
		}
	}

	actual override suspend fun signOut() {
		Firebase.auth.signOut()
	}
}

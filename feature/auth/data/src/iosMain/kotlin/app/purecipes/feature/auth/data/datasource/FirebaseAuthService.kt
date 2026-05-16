package app.purecipes.feature.auth.data.datasource

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

actual class FirebaseAuthService {
	actual suspend fun signInWithEmailAndPassword(email: String, password: String): EmailPasswordSignInResult {
		return try {
			val result = Firebase.auth.signInWithEmailAndPassword(email, password)
			val user = result.user ?: return EmailPasswordSignInResult()
			user.reload()
			if (!user.isEmailVerified) {
				return EmailPasswordSignInResult(emailNotVerified = true)
			}
			EmailPasswordSignInResult(idToken = user.getIdToken(forceRefresh = true))
		} catch (e: RuntimeException) {
			println("Email sign in failed: ${e.message}")
			EmailPasswordSignInResult()
		}
	}

	actual suspend fun createUserWithEmailAndPassword(email: String, password: String) {
		Firebase.auth.createUserWithEmailAndPassword(email, password)
	}

	actual suspend fun sendEmailVerification() {
		Firebase.auth.currentUser?.sendEmailVerification()
	}

	actual suspend fun resendEmailVerification(email: String, password: String) {
		val result = Firebase.auth.signInWithEmailAndPassword(email, password)
		result.user?.sendEmailVerification()
	}

	actual suspend fun signOut() {
		Firebase.auth.signOut()
	}
}

package app.purecipes.feature.auth.data.datasource

actual class FirebaseAuthService {
	actual suspend fun signInWithEmailAndPassword(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual suspend fun createUserWithEmailAndPassword(email: String, password: String) {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual suspend fun sendEmailVerification() {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual suspend fun resendEmailVerification(email: String, password: String) {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual suspend fun signOut() = Unit
}

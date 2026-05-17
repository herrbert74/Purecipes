package app.purecipes.feature.auth.data.datasource

actual class FirebaseAuthService : FirebaseEmailPasswordAuth {
	actual override suspend fun signInWithEmailAndPassword(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual override suspend fun createUserWithEmailAndPassword(email: String, password: String) {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual override suspend fun sendEmailVerification() {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual override suspend fun resendEmailVerification(
		email: String,
		password: String,
	): EmailPasswordSignInResult {
		throw UnsupportedOperationException("Email authentication is not supported on WasmJS")
	}

	actual override suspend fun signOut() = Unit
}

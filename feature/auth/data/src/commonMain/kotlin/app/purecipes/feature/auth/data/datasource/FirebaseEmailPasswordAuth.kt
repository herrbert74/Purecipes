package app.purecipes.feature.auth.data.datasource

internal interface FirebaseEmailPasswordAuth {
	suspend fun signInWithEmailAndPassword(email: String, password: String): EmailPasswordSignInResult

	suspend fun createUserWithEmailAndPassword(email: String, password: String)

	suspend fun sendEmailVerification()

	suspend fun resendEmailVerification(email: String, password: String): EmailPasswordSignInResult

	suspend fun signOut()
}

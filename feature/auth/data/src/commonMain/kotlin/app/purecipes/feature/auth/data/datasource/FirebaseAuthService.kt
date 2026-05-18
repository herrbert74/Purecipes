package app.purecipes.feature.auth.data.datasource

internal expect class FirebaseAuthService() : FirebaseEmailPasswordAuth {
	override suspend fun signInWithEmailAndPassword(email: String, password: String): EmailPasswordSignInResult

	override suspend fun createUserWithEmailAndPassword(email: String, password: String, displayName: String)

	override suspend fun sendEmailVerification()

	override suspend fun resendEmailVerification(email: String, password: String): EmailPasswordSignInResult

	override suspend fun sendPasswordResetEmail(email: String)

	override suspend fun deleteCurrentUser()

	override suspend fun signOut()
}

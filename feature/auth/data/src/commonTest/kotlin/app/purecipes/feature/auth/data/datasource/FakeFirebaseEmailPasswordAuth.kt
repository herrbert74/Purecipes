package app.purecipes.feature.auth.data.datasource

internal class FakeFirebaseEmailPasswordAuth(
	private val signInHandler: suspend (String, String) -> EmailPasswordSignInResult = { _, _ ->
		EmailPasswordSignInResult()
	},
	private val resendHandler: suspend (String, String) -> EmailPasswordSignInResult = { _, _ ->
		EmailPasswordSignInResult()
	},
) : FirebaseEmailPasswordAuth {

	override suspend fun signInWithEmailAndPassword(email: String, password: String): EmailPasswordSignInResult {
		return signInHandler(email, password)
	}

	override suspend fun createUserWithEmailAndPassword(email: String, password: String) = Unit

	override suspend fun sendEmailVerification() = Unit

	override suspend fun resendEmailVerification(email: String, password: String): EmailPasswordSignInResult {
		return resendHandler(email, password)
	}

	override suspend fun signOut() = Unit
}

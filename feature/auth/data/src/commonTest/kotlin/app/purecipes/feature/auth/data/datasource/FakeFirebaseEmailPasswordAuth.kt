package app.purecipes.feature.auth.data.datasource

internal class FakeFirebaseEmailPasswordAuth(
	private val signInHandler: suspend (String, String) -> EmailPasswordSignInResult = { _, _ ->
		EmailPasswordSignInResult()
	},
	private val createUserHandler: suspend (String, String, String) -> Unit = { _, _, _ -> },
	private val resendHandler: suspend (String, String) -> EmailPasswordSignInResult = { _, _ ->
		EmailPasswordSignInResult()
	},
	private val sendPasswordResetHandler: suspend (String) -> Unit = {},
	private val deleteCurrentUserHandler: suspend () -> Unit = {},
) : FirebaseEmailPasswordAuth {

	var lastPasswordResetEmail: String? = null
		private set

	var lastRegisteredDisplayName: String? = null
		private set

	var deleteCurrentUserCallCount = 0
		private set

	var signOutCallCount = 0
		private set

	override suspend fun signInWithEmailAndPassword(email: String, password: String): EmailPasswordSignInResult {
		return signInHandler(email, password)
	}

	override suspend fun createUserWithEmailAndPassword(email: String, password: String, displayName: String) {
		lastRegisteredDisplayName = displayName
		createUserHandler(email, password, displayName)
	}

	override suspend fun sendEmailVerification() = Unit

	override suspend fun resendEmailVerification(email: String, password: String): EmailPasswordSignInResult {
		return resendHandler(email, password)
	}

	override suspend fun sendPasswordResetEmail(email: String) {
		lastPasswordResetEmail = email
		sendPasswordResetHandler(email)
	}

	override suspend fun deleteCurrentUser() {
		deleteCurrentUserCallCount++
		deleteCurrentUserHandler()
	}

	override suspend fun signOut() {
		signOutCallCount++
	}
}

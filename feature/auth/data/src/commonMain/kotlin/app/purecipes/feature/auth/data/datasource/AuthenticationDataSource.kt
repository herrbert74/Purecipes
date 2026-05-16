package app.purecipes.feature.auth.data.datasource

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.AuthenticatedBackendUser
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EmailSignInRequest
import app.purecipes.shared.domain.model.GoogleSignInRequest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthenticationDataSource {
	interface Local {

		val authenticationState: StateFlow<AuthenticationState>

		suspend fun signInWithEmail(email: String, password: String): Outcome<String>

		suspend fun registerWithEmail(
			firstName: String,
			familyName: String,
			email: String,
			password: String,
		): Outcome<Unit>

		suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit>

		suspend fun signInWithBackendSession(session: AuthenticatedSession): Outcome<AuthUser>

		suspend fun signInWithExternalProvider(user: AuthUser): Outcome<AuthUser>

		suspend fun signOut()
	}

	interface Remote {

		suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession>

		suspend fun signInWithEmailToken(idToken: String): Outcome<AuthenticatedSession>

		suspend fun getCurrentSession(): Outcome<AuthenticatedSession>

		suspend fun signOut()
	}
}

class AuthenticationRemoteDataSource(
	private val api: PurecipesApi,
) : AuthenticationDataSource.Remote {

	override suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession> = runCatchingApi {
		api.signInWithGoogle(GoogleSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun signInWithEmailToken(idToken: String): Outcome<AuthenticatedSession> = runCatchingApi {
		api.signInWithEmail(EmailSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun getCurrentSession(): Outcome<AuthenticatedSession> = runCatchingApi {
		api.getCurrentSession()
	}

	override suspend fun signOut(): Unit = runCatchingApi {
		api.signOut()
	}.let { Unit }
}

class AuthenticationStore(initialState: AuthenticationState = AuthenticationState.SignedOut) {

	internal val accountsByEmail = linkedMapOf<String, EmailAccountRecord>()
	val authenticationState = MutableStateFlow(initialState)
}

internal object AuthenticationStoreHolder {

	val store = AuthenticationStore()
}

internal data class EmailAccountRecord(
	val id: String,
	val firstName: String,
	val familyName: String,
	val email: String,
	val password: String,
	val profileImageUrl: String?,
)

class FirebaseAuthenticationLocalDataSource(
	private val store: AuthenticationStore,
	private val sessionTokenStore: SessionTokenStore,
	private val firebaseAuthService: FirebaseAuthService = FirebaseAuthService(),
) : AuthenticationDataSource.Local {

	override val authenticationState: StateFlow<AuthenticationState> = store.authenticationState.asStateFlow()

	override suspend fun signInWithEmail(email: String, password: String): Outcome<String> {
		val normalizedEmail = email.normalizedEmail()
		return try {
			val result = firebaseAuthService.signInWithEmailAndPassword(normalizedEmail, password)
			when {
				result.idToken != null -> Ok(result.idToken)
				result.emailNotVerified -> Err(Failure.ServerError(EMAIL_NOT_VERIFIED_MESSAGE))
				else -> Err(Failure.ServerError("Sign in failed"))
			}
		} catch (e: RuntimeException) {
			Err(Failure.ServerError(e.message ?: "Sign in failed"))
		}
	}

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		return try {
			firebaseAuthService.resendEmailVerification(normalizedEmail, password)
			Ok(Unit)
		} catch (e: RuntimeException) {
			Err(Failure.ServerError(e.message ?: "Could not resend verification email"))
		}
	}

	override suspend fun registerWithEmail(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		return try {
			firebaseAuthService.createUserWithEmailAndPassword(normalizedEmail, password)
			firebaseAuthService.sendEmailVerification()
			Ok(Unit)
		} catch (e: RuntimeException) {
			Err(Failure.ServerError(e.message ?: "Registration failed"))
		}
	}

	override suspend fun signInWithBackendSession(session: AuthenticatedSession): Outcome<AuthUser> {
		sessionTokenStore.saveSession(session)
		val user = session.user.toAuthUser()
		store.authenticationState.value = AuthenticationState.SignedIn(user)
		return Ok(user)
	}

	override suspend fun signInWithExternalProvider(user: AuthUser): Outcome<AuthUser> {
		val normalizedEmail = user.email.normalizedEmail()
		sessionTokenStore.clearSession()
		val existingAccount = store.accountsByEmail[normalizedEmail]
		val resolvedUser = if (existingAccount != null) {
			user.copy(
				email = normalizedEmail,
				displayName = user.displayName.ifBlank { existingAccount.fullName() },
				firstName = user.firstName ?: existingAccount.firstName,
				familyName = user.familyName ?: existingAccount.familyName,
				profileImageUrl = user.profileImageUrl ?: existingAccount.profileImageUrl,
			)
		} else {
			user.copy(
				email = normalizedEmail,
				displayName = user.displayName.ifBlank { normalizedEmail.fallbackDisplayName() },
			)
		}
		store.authenticationState.value = AuthenticationState.SignedIn(resolvedUser)
		return Ok(resolvedUser)
	}

	override suspend fun signOut() {
		sessionTokenStore.clearSession()
		try {
			firebaseAuthService.signOut()
		} catch (e: RuntimeException) {
			println("Firebase sign out ignored: ${e.message}")
		}
		store.authenticationState.value = AuthenticationState.SignedOut
	}
}

class InMemoryAuthenticationLocalDataSource(
	private val store: AuthenticationStore,
	private val sessionTokenStore: SessionTokenStore,
) : AuthenticationDataSource.Local {

	override val authenticationState: StateFlow<AuthenticationState> = store.authenticationState.asStateFlow()

	override suspend fun signInWithEmail(email: String, password: String): Outcome<String> {
		val normalizedEmail = email.normalizedEmail()
		val account = store.accountsByEmail[normalizedEmail]
			?: return Err(Failure.ServerError("No account was found for this email"))
		if (account.password != password) {
			return Err(Failure.ServerError("Incorrect password"))
		}
		// Mock Firebase ID token
		return Ok("mock-firebase-id-token-for-$normalizedEmail")
	}

	override suspend fun registerWithEmail(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		if (store.accountsByEmail.containsKey(normalizedEmail)) {
			return Err(Failure.ServerError("An account already exists for this email"))
		}
		val account = EmailAccountRecord(
			id = normalizedEmail,
			firstName = firstName,
			familyName = familyName,
			email = normalizedEmail,
			password = password,
			profileImageUrl = null,
		)
		store.accountsByEmail[normalizedEmail] = account
		return Ok(Unit)
	}

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> = Ok(Unit)

	override suspend fun signInWithBackendSession(session: AuthenticatedSession): Outcome<AuthUser> {
		sessionTokenStore.saveSession(session)
		val user = session.user.toAuthUser()
		store.authenticationState.value = AuthenticationState.SignedIn(user)
		return Ok(user)
	}

	override suspend fun signInWithExternalProvider(user: AuthUser): Outcome<AuthUser> {
		val normalizedEmail = user.email.normalizedEmail()
		sessionTokenStore.clearSession()
		val existingAccount = store.accountsByEmail[normalizedEmail]
		val resolvedUser = if (existingAccount != null) {
			user.copy(
				email = normalizedEmail,
				displayName = user.displayName.ifBlank { existingAccount.fullName() },
				firstName = user.firstName ?: existingAccount.firstName,
				familyName = user.familyName ?: existingAccount.familyName,
				profileImageUrl = user.profileImageUrl ?: existingAccount.profileImageUrl,
			)
		} else {
			user.copy(
				email = normalizedEmail,
				displayName = user.displayName.ifBlank { normalizedEmail.fallbackDisplayName() },
			)
		}
		store.authenticationState.value = AuthenticationState.SignedIn(resolvedUser)
		return Ok(resolvedUser)
	}

	override suspend fun signOut() {
		sessionTokenStore.clearSession()
		store.authenticationState.value = AuthenticationState.SignedOut
	}
}

private fun EmailAccountRecord.toAuthUser(
	provider: AuthProvider,
	displayName: String = fullName(),
	profileImageUrl: String? = this.profileImageUrl,
): AuthUser {
	return AuthUser(
		id = id,
		email = email,
		displayName = displayName,
		firstName = firstName,
		familyName = familyName,
		profileImageUrl = profileImageUrl,
		provider = provider,
	)
}

private fun EmailAccountRecord.fullName(): String = "$firstName $familyName"

private fun String.normalizedEmail(): String = trim().lowercase()

private fun String.fallbackDisplayName(): String {
	return substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

internal fun AuthenticatedSession.toAuthenticationState(): AuthenticationState {
	return AuthenticationState.SignedIn(user.toAuthUser())
}

private fun AuthenticatedBackendUser.toAuthUser(): AuthUser {
	return AuthUser(
		id = id,
		email = email.trim().lowercase(),
		displayName = displayName,
		firstName = firstName,
		familyName = familyName,
		profileImageUrl = profileImageUrl,
		provider = provider.toAuthProvider(),
	)
}

private fun String.toAuthProvider(): AuthProvider {
	return when (uppercase()) {
		"EMAIL" -> AuthProvider.EMAIL
		"GOOGLE" -> AuthProvider.GOOGLE
		"APPLE" -> AuthProvider.APPLE
		"FACEBOOK" -> AuthProvider.FACEBOOK
		else -> AuthProvider.EMAIL
	}
}

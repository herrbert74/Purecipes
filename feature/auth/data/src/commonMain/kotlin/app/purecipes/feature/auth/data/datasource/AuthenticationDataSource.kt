package app.purecipes.feature.auth.data.datasource

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.toAuthUser
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.EmailSignInRequest
import app.purecipes.shared.domain.model.FacebookSignInRequest
import app.purecipes.shared.domain.model.GoogleSignInRequest
import app.purecipes.shared.domain.model.INCORRECT_EMAIL_OR_PASSWORD_MESSAGE
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthenticationDataSource {
	interface Local {

		val authenticationState: StateFlow<AuthenticationState>

		suspend fun signInWithEmail(email: String, password: String): Outcome<String>

		suspend fun registerWithEmail(
			displayName: String,
			email: String,
			password: String,
		): Outcome<Unit>

		suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit>

		suspend fun sendPasswordResetEmail(email: String): Outcome<Unit>

		suspend fun signInWithBackendSession(session: AuthenticatedSession): Outcome<AuthUser>

		suspend fun signInWithExternalProvider(user: AuthUser): Outcome<AuthUser>

		suspend fun deleteAccount(): Outcome<Unit>

		suspend fun signOut()
	}

	interface Remote {

		suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession>

		suspend fun signInWithFacebook(idToken: String): Outcome<AuthenticatedSession>

		suspend fun signInWithEmailToken(idToken: String): Outcome<AuthenticatedSession>

		suspend fun getCurrentSession(): Outcome<AuthenticatedSession>

		suspend fun signOut()
	}
}

@Inject
@ContributesBinding(AppScope::class)
class AuthenticationRemoteDataSource(
	private val api: PurecipesApi,
) : AuthenticationDataSource.Remote {

	override suspend fun signInWithGoogle(idToken: String): Outcome<AuthenticatedSession> = runCatchingApi {
		api.signInWithGoogle(GoogleSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun signInWithFacebook(idToken: String): Outcome<AuthenticatedSession> = runCatchingApi {
		api.signInWithFacebook(FacebookSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun signInWithEmailToken(idToken: String): Outcome<AuthenticatedSession> = runCatchingApi {
		api.signInWithEmail(EmailSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun getCurrentSession(): Outcome<AuthenticatedSession> = runCatchingApi {
		api.getCurrentSession()
	}

	override suspend fun signOut(): Unit = runCatchingApi {
		api.signOut()
	}.let { }
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
	val displayName: String,
	val email: String,
	val password: String,
	val profileImageUrl: String?,
)

@Inject
@ContributesBinding(AppScope::class)
class FirebaseAuthenticationLocalDataSource(
	private val store: AuthenticationStore,
	private val sessionTokenStore: SessionTokenStore,
	private val firebaseAuthService: FirebaseEmailPasswordAuth = FirebaseAuthService(),
) : AuthenticationDataSource.Local {

	override val authenticationState: StateFlow<AuthenticationState> = store.authenticationState.asStateFlow()

	override suspend fun signInWithEmail(email: String, password: String): Outcome<String> {
		val normalizedEmail = email.normalizedEmail()
		return runCatching {
			firebaseAuthService.signInWithEmailAndPassword(normalizedEmail, password).toSignInOutcome()
		}.fold(
			onSuccess = { it },
			onFailure = { Err(Failure.ServerError(mapEmailPasswordAuthException(it))) },
		)
	}

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		return runCatching {
			firebaseAuthService.resendEmailVerification(normalizedEmail, password).toResendOutcome()
		}.fold(
			onSuccess = { it },
			onFailure = { Err(Failure.ServerError(mapEmailPasswordAuthException(it))) },
		)
	}

	override suspend fun sendPasswordResetEmail(email: String): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		return runCatching {
			firebaseAuthService.sendPasswordResetEmail(normalizedEmail)
			Ok(Unit)
		}.fold(
			onSuccess = { it },
			onFailure = { Err(Failure.ServerError(mapEmailPasswordAuthException(it))) },
		)
	}

	override suspend fun registerWithEmail(
		displayName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		return runCatching {
			firebaseAuthService.createUserWithEmailAndPassword(
				email = normalizedEmail,
				password = password,
				displayName = displayName,
			)
			firebaseAuthService.sendEmailVerification()
			Ok(Unit)
		}.fold(
			onSuccess = { it },
			onFailure = { Err(Failure.ServerError(mapEmailPasswordAuthException(it))) },
		)
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
				displayName = user.displayName.ifBlank { existingAccount.displayName },
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

	override suspend fun deleteAccount(): Outcome<Unit> {
		return runCatching {
			firebaseAuthService.deleteCurrentUser()
		}.fold(
			onSuccess = {
				clearSignedInState()
				Ok(Unit)
			},
			onFailure = { Err(Failure.ServerError(mapEmailPasswordAuthException(it))) },
		)
	}

	override suspend fun signOut() {
		clearSignedInState()
	}

	private suspend fun clearSignedInState() {
		sessionTokenStore.clearSession()
		runCatching {
			firebaseAuthService.signOut()
		}.onFailure {
			println("Firebase sign out ignored: ${it.message}")
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
		return when {
			account == null -> Err(Failure.ServerError("No account was found for this email"))
			account.password != password -> Err(Failure.ServerError(INCORRECT_EMAIL_OR_PASSWORD_MESSAGE))
			else -> Ok("mock-firebase-id-token-for-$normalizedEmail")
		}
	}

	override suspend fun registerWithEmail(
		displayName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		val normalizedEmail = email.normalizedEmail()
		if (store.accountsByEmail.containsKey(normalizedEmail)) {
			return Err(Failure.ServerError("An account already exists for this email"))
		}
		val account = EmailAccountRecord(
			id = normalizedEmail,
			displayName = displayName,
			email = normalizedEmail,
			password = password,
			profileImageUrl = null,
		)
		store.accountsByEmail[normalizedEmail] = account
		return Ok(Unit)
	}

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> = Ok(Unit)

	override suspend fun sendPasswordResetEmail(email: String): Outcome<Unit> = Ok(Unit)

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
				displayName = user.displayName.ifBlank { existingAccount.displayName },
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

	override suspend fun deleteAccount(): Outcome<Unit> {
		val signedInUser = (store.authenticationState.value as? AuthenticationState.SignedIn)?.user
		if (signedInUser?.provider == AuthProvider.EMAIL) {
			store.accountsByEmail.remove(signedInUser.email.normalizedEmail())
		}
		sessionTokenStore.clearSession()
		store.authenticationState.value = AuthenticationState.SignedOut
		return Ok(Unit)
	}

	override suspend fun signOut() {
		sessionTokenStore.clearSession()
		store.authenticationState.value = AuthenticationState.SignedOut
	}
}

private fun String.normalizedEmail(): String = trim().lowercase()

private fun String.fallbackDisplayName(): String {
	return substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

private fun EmailPasswordSignInResult.toSignInOutcome(): Outcome<String> {
	return when {
		idToken != null -> Ok(idToken)
		emailNotVerified -> Err(Failure.ServerError(EMAIL_NOT_VERIFIED_MESSAGE))
		errorMessage != null -> Err(Failure.ServerError(errorMessage))
		else -> Err(Failure.ServerError("Sign in failed"))
	}
}

private fun EmailPasswordSignInResult.toResendOutcome(): Outcome<Unit> {
	return when {
		errorMessage != null -> Err(Failure.ServerError(errorMessage))
		else -> Ok(Unit)
	}
}

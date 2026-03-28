package com.purecipes.feature.auth.data.datasource

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.VerifiedGoogleUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AuthenticationDataSource {
	interface Local {

		val authenticationState: StateFlow<AuthenticationState>

		suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser>

		suspend fun registerWithEmail(
			firstName: String,
			familyName: String,
			email: String,
			password: String,
		): Outcome<AuthUser>

		suspend fun signInWithGoogle(user: AuthUser): Outcome<AuthUser>

		suspend fun signOut()
	}

	interface Remote {

		suspend fun signInWithGoogle(idToken: String): Outcome<VerifiedGoogleUser>

		suspend fun signOut()
	}
}

class AuthenticationRemoteDataSource(
	private val api: PurecipesApi,
) : AuthenticationDataSource.Remote {

	override suspend fun signInWithGoogle(idToken: String): Outcome<VerifiedGoogleUser> = runCatchingApi {
		api.signInWithGoogle(GoogleSignInRequest(idToken = idToken.trim()))
	}

	override suspend fun signOut() = Unit
}

class AuthenticationStore {

	internal val accountsByEmail = linkedMapOf<String, EmailAccountRecord>()
	val authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.SignedOut)
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

class InMemoryAuthenticationLocalDataSource(
	private val store: AuthenticationStore,
) : AuthenticationDataSource.Local {

	override val authenticationState: StateFlow<AuthenticationState> = store.authenticationState.asStateFlow()

	override suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser> {
		val normalizedEmail = email.normalizedEmail()
		val account = store.accountsByEmail[normalizedEmail]
			?: return Err(Failure.ServerError("No account was found for this email"))
		if (account.password != password) {
			return Err(Failure.ServerError("Incorrect password"))
		}
		val user = account.toAuthUser(provider = AuthProvider.EMAIL)
		store.authenticationState.value = AuthenticationState.SignedIn(user)
		return Ok(user)
	}

	override suspend fun registerWithEmail(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<AuthUser> {
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
		val user = account.toAuthUser(provider = AuthProvider.EMAIL)
		store.authenticationState.value = AuthenticationState.SignedIn(user)
		return Ok(user)
	}

	override suspend fun signInWithGoogle(user: AuthUser): Outcome<AuthUser> {
		val normalizedEmail = user.email.normalizedEmail()
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

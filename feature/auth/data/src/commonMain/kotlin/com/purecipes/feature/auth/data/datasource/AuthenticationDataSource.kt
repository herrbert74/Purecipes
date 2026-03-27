package com.purecipes.feature.auth.data.datasource

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
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

		suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser>

		suspend fun signOut()
	}
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

	override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> {
		val email = profile.email?.normalizedEmail()
			?: return Err(Failure.ServerError("Google did not return an email address"))
		val existingAccount = store.accountsByEmail[email]
		val user = if (existingAccount != null) {
			existingAccount.toAuthUser(
				provider = AuthProvider.GOOGLE,
				displayName = profile.displayName.ifBlank { existingAccount.fullName() },
				profileImageUrl = profile.profileImageUrl,
			)
		} else {
			profile.toAuthUser(email)
		}
		store.authenticationState.value = AuthenticationState.SignedIn(user)
		return Ok(user)
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

private fun GoogleAuthenticationProfile.toAuthUser(email: String): AuthUser {
	val names = splitDisplayName(displayName)
	val resolvedDisplayName = displayName.ifBlank {
		email.substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
	}
	return AuthUser(
		id = email,
		email = email,
		displayName = resolvedDisplayName,
		firstName = names.first,
		familyName = names.second,
		profileImageUrl = profileImageUrl,
		provider = AuthProvider.GOOGLE,
	)
}

private fun splitDisplayName(displayName: String): Pair<String?, String?> {
	val parts = displayName
		.trim()
		.split(' ')
		.filter { it.isNotBlank() }
	return when {
		parts.isEmpty() -> null to null
		parts.size == 1 -> parts.first() to null
		else -> parts.first() to parts.drop(1).joinToString(" ")
	}
}

private fun EmailAccountRecord.fullName(): String = "$firstName $familyName"

private fun String.normalizedEmail(): String = trim().lowercase()

package com.purecipes.feature.auth.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.StateFlow

class AuthenticationAccessor(
	private val localDataSource: AuthenticationDataSource.Local,
	private val remoteDataSource: AuthenticationDataSource.Remote,
) : AuthenticationRepository {

	override val authenticationState: StateFlow<AuthenticationState> = localDataSource.authenticationState

	override suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser> {
		return localDataSource.signInWithEmail(email, password)
	}

	override suspend fun registerWithEmail(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<AuthUser> {
		return localDataSource.registerWithEmail(firstName, familyName, email, password)
	}

	override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> {
		val verifiedUserResult = remoteDataSource.signInWithGoogle(profile.idToken)
		val failure = verifiedUserResult.getError()
		if (failure != null) {
			return Err(failure)
		}
		val verifiedUser = verifiedUserResult.get()
			?: return Err(Failure.UnexpectedFailure)
		return localDataSource.signInWithBackendSession(verifiedUser)
	}

	override suspend fun signInWithExternalProvider(profile: ExternalAuthenticationProfile): Outcome<AuthUser> {
		return localDataSource.signInWithExternalProvider(profile.toAuthUser())
	}

	override suspend fun signOut() {
		localDataSource.signOut()
	}
}

private fun ExternalAuthenticationProfile.toAuthUser(): AuthUser {
	val normalizedEmail = email?.trim()?.lowercase().orEmpty()
	val resolvedDisplayName = displayName?.trim().takeUnless { it.isNullOrBlank() }
		?: normalizedEmail.substringBefore('@').replaceFirstChar {
			if (it.isLowerCase()) it.titlecase() else it.toString()
		}
	return AuthUser(
		id = id.trim(),
		email = normalizedEmail,
		displayName = resolvedDisplayName,
		firstName = null,
		familyName = null,
		profileImageUrl = profileImageUrl,
		provider = provider,
	)
}

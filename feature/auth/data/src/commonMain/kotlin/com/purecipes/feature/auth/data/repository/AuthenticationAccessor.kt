package com.purecipes.feature.auth.data.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.StateFlow

class AuthenticationAccessor(
	private val localDataSource: AuthenticationDataSource.Local,
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
		return localDataSource.signInWithGoogle(profile)
	}

	override suspend fun signOut() {
		localDataSource.signOut()
	}
}

package com.purecipes.feature.auth.domain.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationRepository {

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

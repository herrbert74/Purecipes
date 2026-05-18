package app.purecipes.feature.auth.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthenticationRepository {

	val authenticationState: StateFlow<AuthenticationState>

	suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser>

	suspend fun registerWithEmail(
		displayName: String,
		email: String,
		password: String,
	): Outcome<Unit>

	suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit>

	suspend fun sendPasswordResetEmail(email: String): Outcome<Unit>

	suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser>

	suspend fun signInWithExternalProvider(profile: ExternalAuthenticationProfile): Outcome<AuthUser>

	suspend fun deleteAccount(): Outcome<Unit>

	suspend fun signOut()
}

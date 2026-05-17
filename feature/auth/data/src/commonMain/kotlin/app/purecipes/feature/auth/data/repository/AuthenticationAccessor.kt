package app.purecipes.feature.auth.data.repository

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.flow.StateFlow

class AuthenticationAccessor(
	private val localDataSource: AuthenticationDataSource.Local,
	private val remoteDataSource: AuthenticationDataSource.Remote,
) : AuthenticationRepository {

	override val authenticationState: StateFlow<AuthenticationState> = localDataSource.authenticationState

	override suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser> =
		localDataSource.signInWithEmail(email, password)
			.mapFailureUserMessage()
			.andThen { firebaseToken ->
				remoteDataSource.signInWithEmailToken(firebaseToken).mapFailureUserMessage()
			}
			.andThen { session ->
				localDataSource.signInWithBackendSession(session)
			}

	override suspend fun registerWithEmail(
		displayName: String,
		email: String,
		password: String,
	): Outcome<Unit> = localDataSource.registerWithEmail(displayName, email, password).map { }

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> =
		localDataSource.resendEmailVerification(email, password)
			.mapFailureUserMessage()
			.map { }

	override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> =
		remoteDataSource.signInWithGoogle(profile.idToken)
			.andThen { session ->
				localDataSource.signInWithBackendSession(session)
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

private fun <T> Outcome<T>.mapFailureUserMessage(): Outcome<T> = mapError { failure ->
	failure.withUserAuthMessage()
}

private fun Failure.withUserAuthMessage(): Failure {
	if (this !is Failure.ServerError) {
		return this
	}
	val normalizedMessage = message.normalizeAuthMessage()
	if (normalizedMessage == message) {
		return this
	}
	return Failure.ServerError(normalizedMessage)
}

private fun String.normalizeAuthMessage(): String {
	if (contains("email", ignoreCase = true) && contains("verif", ignoreCase = true)) {
		return EMAIL_NOT_VERIFIED_MESSAGE
	}
	return this
}

package app.purecipes.feature.auth.data.repository

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.FacebookAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.model.toAuthUser
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.onOk
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
@ContributesBinding(AppScope::class)
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

	override suspend fun sendPasswordResetEmail(email: String): Outcome<Unit> =
		localDataSource.sendPasswordResetEmail(email)
			.mapFailureUserMessage()
			.map { }

	override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> =
		remoteDataSource.signInWithGoogle(profile.idToken)
			.andThen { session ->
				localDataSource.signInWithBackendSession(session)
			}

	override suspend fun signInWithFacebook(profile: FacebookAuthenticationProfile): Outcome<AuthUser> =
		remoteDataSource.signInWithFacebook(profile.idToken)
			.andThen { session ->
				localDataSource.signInWithBackendSession(session)
			}

	override suspend fun signInWithExternalProvider(profile: ExternalAuthenticationProfile): Outcome<AuthUser> {
		return localDataSource.signInWithExternalProvider(profile.toAuthUser())
	}

	override suspend fun deleteAccount(): Outcome<Unit> =
		localDataSource.deleteAuthenticationIdentity()
			.mapFailureUserMessage()
			.andThen { remoteDataSource.deleteAccount() }
			.onOk { localDataSource.signOut() }

	override suspend fun signOut() {
		localDataSource.signOut()
	}
}

private fun <T> Outcome<T>.mapFailureUserMessage(): Outcome<T> = mapError { failure ->
	failure.withUserAuthMessage()
}

private fun Failure.withUserAuthMessage(): Failure {
	if (this !is Failure.ServerError) {
		return this
	}
	val normalizedMessage = message.normalizeAuthMessage()
	return if (normalizedMessage == message) {
		this
	} else {
		Failure.ServerError(normalizedMessage)
	}
}

private fun String.normalizeAuthMessage(): String {
	if (contains("email", ignoreCase = true) && contains("verif", ignoreCase = true)) {
		return EMAIL_NOT_VERIFIED_MESSAGE
	}
	return this
}

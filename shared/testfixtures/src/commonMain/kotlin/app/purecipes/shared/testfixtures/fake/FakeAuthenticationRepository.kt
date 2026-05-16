package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeAuthenticationRepository(
	initialState: AuthenticationState = AuthenticationState.SignedOut,
	private val signInWithEmailHandler: suspend (String, String) -> Outcome<AuthUser> = { _, _ ->
		Err(Failure.ServerError("Not used"))
	},
	private val registerWithEmailHandler: suspend (
		String,
		String,
		String,
		String,
	) -> Outcome<Unit> = { _, _, _, _ ->
		Ok(Unit)
	},
	private val signInWithGoogleHandler: suspend (GoogleAuthenticationProfile) -> Outcome<AuthUser> = { profile ->
		Ok(
			AuthUser(
				id = profile.idToken,
				email = profile.email.orEmpty(),
				displayName = profile.displayName,
				firstName = null,
				familyName = null,
				profileImageUrl = profile.profileImageUrl,
				provider = AuthProvider.GOOGLE,
			),
		)
	},
	private val signInWithExternalProviderHandler: suspend (
		ExternalAuthenticationProfile,
	) -> Outcome<AuthUser> = { profile ->
		Ok(
			AuthUser(
				id = profile.id,
				email = profile.email.orEmpty(),
				displayName = profile.displayName.orEmpty(),
				firstName = null,
				familyName = null,
				profileImageUrl = profile.profileImageUrl,
				provider = profile.provider,
			),
		)
	},
) : AuthenticationRepository {

	private val mutableAuthenticationState = MutableStateFlow(initialState)

	override val authenticationState: StateFlow<AuthenticationState> = mutableAuthenticationState

	override suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser> {
		return signInWithEmailHandler(email, password).also(::updateAuthenticationState)
	}

	override suspend fun registerWithEmail(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		return registerWithEmailHandler(
			firstName,
			familyName,
			email,
			password,
		)
	}

	override suspend fun resendEmailVerification(email: String, password: String): Outcome<Unit> = Ok(Unit)

	override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> {
		return signInWithGoogleHandler(profile).also(::updateAuthenticationState)
	}

	override suspend fun signInWithExternalProvider(profile: ExternalAuthenticationProfile): Outcome<AuthUser> {
		return signInWithExternalProviderHandler(profile).also(::updateAuthenticationState)
	}

	override suspend fun signOut() {
		mutableAuthenticationState.value = AuthenticationState.SignedOut
	}

	private fun updateAuthenticationState(result: Outcome<AuthUser>) {
		result.get()?.let { user ->
			mutableAuthenticationState.value = AuthenticationState.SignedIn(user)
		}
	}
}

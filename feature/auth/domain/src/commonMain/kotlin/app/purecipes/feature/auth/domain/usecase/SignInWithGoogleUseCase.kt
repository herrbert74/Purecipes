package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.Inject

@Inject
class SignInWithGoogleUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(profile: GoogleAuthenticationProfile): Outcome<AuthUser> {
		if (profile.idToken.isBlank()) {
			return Err(Failure.ServerError("Google sign-in did not return an ID token"))
		}
		return repository.signInWithGoogle(profile)
	}
}

package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.Inject

@Inject
class SignInWithExternalProviderUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(profile: ExternalAuthenticationProfile): Outcome<AuthUser> {
		if (profile.id.isBlank()) {
			return Err(
				Failure.ServerError("${profile.provider.providerDisplayName()} sign-in did not return a user id")
			)
		}
		val email = profile.email?.trim().orEmpty()
		if (email.isBlank()) {
			return Err(
				Failure.ServerError("${profile.provider.providerDisplayName()} sign-in did not return an email address")
			)
		}
		return repository.signInWithExternalProvider(
			profile.copy(
				id = profile.id.trim(),
				email = email,
				displayName = profile.displayName?.trim(),
				profileImageUrl = profile.profileImageUrl?.trim()?.takeIf { it.isNotBlank() },
			),
		)
	}
}

private fun AuthProvider.providerDisplayName(): String {
	return when (this) {
		AuthProvider.EMAIL -> "Email"
		AuthProvider.GOOGLE -> "Google"
		AuthProvider.APPLE -> "Apple"
		AuthProvider.FACEBOOK -> "Facebook"
	}
}

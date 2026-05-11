package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import app.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import kotlinx.coroutines.flow.StateFlow

class ObserveAuthenticationStateUseCase(
	private val repository: AuthenticationRepository,
) {

	operator fun invoke(): StateFlow<AuthenticationState> = repository.authenticationState
}

class SignInWithEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(email: String, password: String): Outcome<AuthUser> {
		val validationError = validateEmailCredentials(email = email, password = password)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.signInWithEmail(email.trim(), password)
	}
}

class RegisterWithEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<AuthUser> {
		val validationError = validateRegistration(
			firstName = firstName,
			familyName = familyName,
			email = email,
			password = password,
		)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.registerWithEmail(
				firstName = firstName.trim(),
				familyName = familyName.trim(),
				email = email.trim(),
				password = password,
			)
	}
}

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

class SignOutUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke() {
		repository.signOut()
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

private fun validateEmailCredentials(email: String, password: String): String? {
	return when {
		email.isBlank() -> "Email is required"
		!email.contains('@') -> "Enter a valid email address"
		password.isBlank() -> "Password is required"
		else -> null
	}
}

private fun validateRegistration(
	firstName: String,
	familyName: String,
	email: String,
	password: String,
): String? {
	return when {
		firstName.isBlank() -> "First name is required"
		familyName.isBlank() -> "Family name is required"
		else -> validateEmailCredentials(email = email, password = password)
	}
}

package com.purecipes.feature.auth.domain.usecase

import com.github.michaelbull.result.Err
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
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

class SignOutUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke() {
		repository.signOut()
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

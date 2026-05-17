package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err

class RegisterWithEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(
		firstName: String,
		familyName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
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

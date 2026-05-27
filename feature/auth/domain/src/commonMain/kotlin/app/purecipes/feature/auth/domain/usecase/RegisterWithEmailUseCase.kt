package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.Inject

@Inject
class RegisterWithEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(
		displayName: String,
		email: String,
		password: String,
	): Outcome<Unit> {
		val validationError = validateRegistration(
			displayName = displayName,
			email = email,
			password = password,
		)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.registerWithEmail(
				displayName = displayName.trim(),
				email = email.trim(),
				password = password,
			)
	}
}

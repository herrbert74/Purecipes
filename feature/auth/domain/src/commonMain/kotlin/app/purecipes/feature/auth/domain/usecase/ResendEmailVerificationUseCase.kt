package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err

class ResendEmailVerificationUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(email: String, password: String): Outcome<Unit> {
		val validationError = validateEmailCredentials(email = email, password = password)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.resendEmailVerification(email.trim(), password)
	}
}

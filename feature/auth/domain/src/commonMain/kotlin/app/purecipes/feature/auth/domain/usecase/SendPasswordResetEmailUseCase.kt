package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.Inject

@Inject
class SendPasswordResetEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(email: String): Outcome<Unit> {
		val validationError = validateEmail(email)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.sendPasswordResetEmail(email.trim())
	}
}

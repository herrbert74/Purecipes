package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.github.michaelbull.result.Err
import dev.zacsweers.metro.Inject

@Inject
class SignInWithEmailUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(email: String, password: String): Outcome<AuthUser> {
		val validationError = validateEmailCredentials(email = email, password = password)
		return validationError?.let { Err(Failure.ServerError(it)) }
			?: repository.signInWithEmail(email.trim(), password)
	}
}

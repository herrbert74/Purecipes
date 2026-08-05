package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import dev.zacsweers.metro.Inject

@Inject
class ValidateSessionUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke() {
		repository.validateSession()
	}
}

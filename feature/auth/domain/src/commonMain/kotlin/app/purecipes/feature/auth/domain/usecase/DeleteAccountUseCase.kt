package app.purecipes.feature.auth.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository

class DeleteAccountUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke(): Outcome<Unit> {
		return repository.deleteAccount()
	}
}

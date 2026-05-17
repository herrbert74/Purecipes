package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.repository.AuthenticationRepository

class SignOutUseCase(
	private val repository: AuthenticationRepository,
) {

	suspend operator fun invoke() {
		repository.signOut()
	}
}

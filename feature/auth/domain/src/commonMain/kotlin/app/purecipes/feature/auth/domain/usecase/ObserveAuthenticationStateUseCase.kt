package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAuthenticationStateUseCase(
	private val repository: AuthenticationRepository,
) {

	operator fun invoke(): StateFlow<AuthenticationState> = repository.authenticationState
}

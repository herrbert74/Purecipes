package app.purecipes.feature.auth.domain.usecase

import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.StateFlow

@Inject
class ObserveAuthenticationStateUseCase(
	private val repository: AuthenticationRepository,
) {

	operator fun invoke(): StateFlow<AuthenticationState> = repository.authenticationState
}

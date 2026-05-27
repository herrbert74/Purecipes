package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ObserveIncomingLinksUseCase(
	private val incomingLinkRepository: IncomingLinkRepository,
) {

	operator fun invoke(): Flow<PurecipesLink> = incomingLinkRepository.observeLinks()
}

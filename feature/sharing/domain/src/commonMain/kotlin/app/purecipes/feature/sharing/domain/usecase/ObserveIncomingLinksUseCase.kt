package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import kotlinx.coroutines.flow.Flow

class ObserveIncomingLinksUseCase(
	private val incomingLinkRepository: IncomingLinkRepository,
) {

	operator fun invoke(): Flow<PurecipesLink> = incomingLinkRepository.observeLinks()
}

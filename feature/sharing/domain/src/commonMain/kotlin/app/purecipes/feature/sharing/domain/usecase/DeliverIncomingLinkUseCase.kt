package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import dev.zacsweers.metro.Inject

@Inject
class DeliverIncomingLinkUseCase(
	private val incomingLinkRepository: IncomingLinkRepository,
) {

	operator fun invoke(url: String) {
		incomingLinkRepository.deliver(url)
	}
}

package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository

class DeliverIncomingLinkUseCase(
	private val incomingLinkRepository: IncomingLinkRepository,
) {

	operator fun invoke(url: String) {
		incomingLinkRepository.deliver(url)
	}
}

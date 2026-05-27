package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import dev.zacsweers.metro.Inject

@Inject
class PublishWebLaunchLinkUseCase(
	private val webLaunchLinkRepository: WebLaunchLinkRepository,
	private val incomingLinkRepository: IncomingLinkRepository,
) {

	operator fun invoke() {
		val launchUrl = webLaunchLinkRepository.readLaunchUrl() ?: return
		incomingLinkRepository.deliver(launchUrl)
	}
}

package app.purecipes.umbrella

import app.purecipes.feature.sharing.domain.usecase.DeliverIncomingLinkUseCase

object IosIncomingLinkHandler {

	private var deliverIncomingLink: DeliverIncomingLinkUseCase? = null

	fun install(deliverIncomingLinkUseCase: DeliverIncomingLinkUseCase) {
		deliverIncomingLink = deliverIncomingLinkUseCase
	}

	fun handle(url: String) {
		deliverIncomingLink?.invoke(url)
	}
}

package app.purecipes.umbrella

import dev.zacsweers.metro.createGraph

object IosSubscriptionInitializer {

	fun initialize() {
		val graph = createGraph<IosAppGraph>()
		graph.initializeSubscriptionsUseCase()
	}
}

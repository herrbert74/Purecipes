package app.purecipes.umbrella

import dev.zacsweers.metro.createGraph

object IosAdsInitializer {

	fun initialize() {
		val graph = createGraph<IosAppGraph>()
		graph.initializeAdsUseCase()
	}
}

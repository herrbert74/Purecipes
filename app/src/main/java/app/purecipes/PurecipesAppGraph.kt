package app.purecipes

import app.purecipes.app.graph.CommonAppGraph
import app.purecipes.feature.settings.domain.usecase.InitializeNotificationsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface PurecipesAppGraph : CommonAppGraph {

	val initializeNotificationsUseCase: InitializeNotificationsUseCase
}

package app.purecipes.umbrella

import app.purecipes.app.graph.CommonAppGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface IosAppGraph : CommonAppGraph

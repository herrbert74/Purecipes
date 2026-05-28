package app.purecipes.feature.main.ui

import app.purecipes.app.graph.CommonAppGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
internal interface DeviceTestAppGraph : CommonAppGraph

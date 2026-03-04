package com.purecipes

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface PurecipesAppGraph {
	fun inject(activity: MainActivity)
}

package com.purecipes.umbrella

import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface IosAppGraph : DataNetworkModule {
	val recipeSearchRepository: RecipeSearchRepository
}

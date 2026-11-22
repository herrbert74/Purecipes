package app.purecipes.feature.main.ui

import app.purecipes.app.graph.CommonAppGraph
import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
internal interface TestAppGraph : CommonAppGraph {

	val recipeSearchFilterRepository: RecipeSearchFilterRepository
}

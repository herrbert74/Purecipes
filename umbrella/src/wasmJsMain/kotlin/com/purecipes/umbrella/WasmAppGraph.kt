package com.purecipes.umbrella

import com.purecipes.feature.recipedetails.data.repository.RecipeDetailsDataModule
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.search.data.repository.SearchDataModule
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WasmAppGraph : DataNetworkModule, RecipeDetailsDataModule, SearchDataModule {

	val recipeDetailsRepository: RecipeDetailsRepository

	val recipeSearchRepository: RecipeSearchRepository
}

package com.purecipes.umbrella

import com.purecipes.feature.recipedetails.data.repository.RecipeDetailsDataModule
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.feature.search.data.repository.SearchDataModule
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WasmAppGraph : DataNetworkModule, RecipeDetailsDataModule, SearchDataModule {

	val getRecipeDetailsUseCase: GetRecipeDetailsUseCase

	val searchRecipesUseCase: SearchRecipesUseCase
}

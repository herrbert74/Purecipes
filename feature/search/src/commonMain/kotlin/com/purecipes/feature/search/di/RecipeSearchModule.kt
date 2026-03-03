package com.purecipes.feature.search.di

import com.purecipes.feature.search.repository.RecipeSearchRepository
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface RecipeSearchModule {

	@Provides
	fun provideRecipeSearchRepository(api: PurecipesApi): RecipeSearchRepository {
		return RecipeSearchRepository(api)
	}
}

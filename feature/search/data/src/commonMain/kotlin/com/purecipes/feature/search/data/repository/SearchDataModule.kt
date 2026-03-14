package com.purecipes.feature.search.data.repository

import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SearchDataModule {
	@Provides
	fun provideRecipeSearchRepository(api: PurecipesApi): RecipeSearchRepository {
		return RecipeSearchAccessor(api)
	}
}

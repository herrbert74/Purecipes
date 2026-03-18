package com.purecipes.feature.recipedetails.data.repository

import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface RecipeDetailsDataModule {

	@Provides
	fun provideRecipeDetailsRepository(api: PurecipesApi): RecipeDetailsRepository {
		return RecipeDetailsAccessor(api)
	}
}

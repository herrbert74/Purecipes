package com.purecipes.feature.recipedetails.data.repository

import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsDataSource
import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface RecipeDetailsDataModule {

	@Provides
	fun provideRecipeDetailsRemoteDataSource(api: PurecipesApi): RecipeDetailsDataSource.Remote {
		return RecipeDetailsRemoteDataSource(api)
	}

	@Provides
	fun provideRecipeDetailsRepository(remoteDataSource: RecipeDetailsDataSource.Remote): RecipeDetailsRepository {
		return RecipeDetailsAccessor(remoteDataSource)
	}

	@Provides
	fun provideGetRecipeDetailsUseCase(repository: RecipeDetailsRepository): GetRecipeDetailsUseCase {
		return GetRecipeDetailsUseCase(repository)
	}
}

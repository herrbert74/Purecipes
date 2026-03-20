package com.purecipes.feature.search.data.repository

import com.purecipes.feature.search.data.datasource.RecipeSearchDataSource
import com.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SearchDataModule {
	@Provides
	fun provideRecipeSearchRemoteDataSource(api: PurecipesApi): RecipeSearchDataSource.Remote {
		return RecipeSearchRemoteDataSource(api)
	}

	@Provides
	fun provideRecipeSearchRepository(remoteDataSource: RecipeSearchDataSource.Remote): RecipeSearchRepository {
		return RecipeSearchAccessor(remoteDataSource)
	}

	@Provides
	fun provideSearchRecipesUseCase(repository: RecipeSearchRepository): SearchRecipesUseCase {
		return SearchRecipesUseCase(repository)
	}
}

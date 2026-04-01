package com.purecipes.feature.newrecipe.data.repository

import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeRemoteDataSource
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface NewRecipeDataModule {

	@Provides
	fun provideCreatedRecipeRemoteDataSource(api: PurecipesApi): CreatedRecipeDataSource.Remote {
		return CreatedRecipeRemoteDataSource(api)
	}

	@Provides
	fun provideCreatedRecipeRepository(remoteDataSource: CreatedRecipeDataSource.Remote): CreatedRecipeRepository {
		return CreatedRecipeAccessor(remoteDataSource)
	}

	@Provides
	fun provideGetCreatedRecipesUseCase(repository: CreatedRecipeRepository): GetCreatedRecipesUseCase {
		return GetCreatedRecipesUseCase(repository)
	}

	@Provides
	fun provideSaveCreatedRecipeUseCase(repository: CreatedRecipeRepository): SaveCreatedRecipeUseCase {
		return SaveCreatedRecipeUseCase(repository)
	}
}

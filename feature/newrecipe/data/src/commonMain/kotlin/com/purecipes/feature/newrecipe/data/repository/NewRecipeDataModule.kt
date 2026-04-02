package com.purecipes.feature.newrecipe.data.repository

import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeRemoteDataSource
import com.purecipes.feature.newrecipe.data.image.PlatformRecipeImagePathLoader
import com.purecipes.feature.newrecipe.data.image.RecipeImagePathLoader
import com.purecipes.feature.newrecipe.data.image.RecipeImageRemoteUploader
import com.purecipes.feature.newrecipe.data.image.RecipeImageUploader
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.data.config.PurecipesConfig
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

@ContributesTo(AppScope::class)
interface NewRecipeDataModule {

	@Provides
	fun provideCreatedRecipeImagePathLoader(): RecipeImagePathLoader {
		return PlatformRecipeImagePathLoader()
	}

	@Provides
	fun provideRecipeImageUploader(httpClient: HttpClient, purecipesConfig: PurecipesConfig): RecipeImageUploader {
		return RecipeImageRemoteUploader(httpClient, purecipesConfig)
	}

	@Provides
	fun provideCreatedRecipeRemoteDataSource(
		api: PurecipesApi,
		imagePathLoader: RecipeImagePathLoader,
		imageUploader: RecipeImageUploader,
	): CreatedRecipeDataSource.Remote {
		return CreatedRecipeRemoteDataSource(
			api = api,
			imagePathLoader = imagePathLoader,
			imageUploader = imageUploader,
		)
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

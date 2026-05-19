package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import app.purecipes.feature.newrecipe.data.datasource.CreatedRecipeRemoteDataSource
import app.purecipes.feature.newrecipe.data.datasource.RecipeNutritionEstimateDataSource
import app.purecipes.feature.newrecipe.data.datasource.RecipeNutritionEstimateRemoteDataSource
import app.purecipes.feature.newrecipe.data.image.PlatformRecipeImagePathLoader
import app.purecipes.feature.newrecipe.data.image.RecipeImagePathLoader
import app.purecipes.feature.newrecipe.data.image.RecipeImageRemoteUploader
import app.purecipes.feature.newrecipe.data.image.RecipeImageUploader
import app.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import app.purecipes.feature.newrecipe.domain.repository.RecipeNutritionEstimateRepository
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.network.PurecipesApi
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

	@Provides
	fun provideRecipeNutritionEstimateRemoteDataSource(api: PurecipesApi): RecipeNutritionEstimateDataSource.Remote {
		return RecipeNutritionEstimateRemoteDataSource(api)
	}

	@Provides
	fun provideRecipeNutritionEstimateRepository(
		remoteDataSource: RecipeNutritionEstimateDataSource.Remote,
	): RecipeNutritionEstimateRepository {
		return RecipeNutritionEstimateAccessor(remoteDataSource)
	}

	@Provides
	fun provideEstimateRecipeNutritionUseCase(
		repository: RecipeNutritionEstimateRepository,
	): EstimateRecipeNutritionUseCase {
		return EstimateRecipeNutritionUseCase(repository)
	}
}

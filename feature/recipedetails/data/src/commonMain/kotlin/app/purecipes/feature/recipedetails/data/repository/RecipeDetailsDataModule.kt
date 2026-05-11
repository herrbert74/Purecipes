package app.purecipes.feature.recipedetails.data.repository

import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsDataSource
import app.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.data.network.PurecipesApi
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

package com.purecipes.feature.favorites.data.repository

import com.purecipes.feature.favorites.data.datasource.CookbooksDataSource
import com.purecipes.feature.favorites.data.datasource.CookbooksRemoteDataSource
import com.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import com.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveRecipeFromCookbookUseCase
import com.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FavoritesDataModule {

	@Provides
	fun provideFavoritesRemoteDataSource(api: PurecipesApi): FavoritesDataSource.Remote {
		return FavoritesRemoteDataSource(api)
	}

	@Provides
	fun provideCookbooksRemoteDataSource(api: PurecipesApi): CookbooksDataSource.Remote {
		return CookbooksRemoteDataSource(api)
	}

	@Provides
	fun provideFavoritesRepository(remoteDataSource: FavoritesDataSource.Remote): FavoritesRepository {
		return FavoritesAccessor(remoteDataSource)
	}

	@Provides
	fun provideCookbooksRepository(remoteDataSource: CookbooksDataSource.Remote): CookbooksRepository {
		return CookbooksAccessor(remoteDataSource)
	}

	@Provides
	fun provideAddFavoriteRecipeUseCase(repository: FavoritesRepository): AddFavoriteRecipeUseCase {
		return AddFavoriteRecipeUseCase(repository)
	}

	@Provides
	fun provideGetFavoriteRecipesPageUseCase(repository: FavoritesRepository): GetFavoriteRecipesPageUseCase {
		return GetFavoriteRecipesPageUseCase(repository)
	}

	@Provides
	fun provideRemoveFavoriteRecipeUseCase(repository: FavoritesRepository): RemoveFavoriteRecipeUseCase {
		return RemoveFavoriteRecipeUseCase(repository)
	}

	@Provides
	fun provideGetCookbooksPageUseCase(repository: CookbooksRepository): GetCookbooksPageUseCase {
		return GetCookbooksPageUseCase(repository)
	}

	@Provides
	fun provideCreateCookbookUseCase(repository: CookbooksRepository): CreateCookbookUseCase {
		return CreateCookbookUseCase(repository)
	}

	@Provides
	fun provideDeleteCookbookUseCase(repository: CookbooksRepository): DeleteCookbookUseCase {
		return DeleteCookbookUseCase(repository)
	}

	@Provides
	fun provideGetCookbookRecipesPageUseCase(repository: CookbooksRepository): GetCookbookRecipesPageUseCase {
		return GetCookbookRecipesPageUseCase(repository)
	}

	@Provides
	fun provideAddRecipeToCookbookUseCase(repository: CookbooksRepository): AddRecipeToCookbookUseCase {
		return AddRecipeToCookbookUseCase(repository)
	}

	@Provides
	fun provideRemoveRecipeFromCookbookUseCase(repository: CookbooksRepository): RemoveRecipeFromCookbookUseCase {
		return RemoveRecipeFromCookbookUseCase(repository)
	}

	@Provides
	fun provideGetRecipeCookbooksUseCase(repository: CookbooksRepository): GetRecipeCookbooksUseCase {
		return GetRecipeCookbooksUseCase(repository)
	}
}

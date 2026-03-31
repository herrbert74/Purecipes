package com.purecipes.feature.favorites.data.repository

import com.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import com.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
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
	fun provideFavoritesRepository(remoteDataSource: FavoritesDataSource.Remote): FavoritesRepository {
		return FavoritesAccessor(remoteDataSource)
	}

	@Provides
	fun provideAddFavoriteRecipeUseCase(repository: FavoritesRepository): AddFavoriteRecipeUseCase {
		return AddFavoriteRecipeUseCase(repository)
	}

	@Provides
	fun provideGetFavoriteRecipesUseCase(repository: FavoritesRepository): GetFavoriteRecipesUseCase {
		return GetFavoriteRecipesUseCase(repository)
	}

	@Provides
	fun provideRemoveFavoriteRecipeUseCase(repository: FavoritesRepository): RemoveFavoriteRecipeUseCase {
		return RemoveFavoriteRecipeUseCase(repository)
	}
}

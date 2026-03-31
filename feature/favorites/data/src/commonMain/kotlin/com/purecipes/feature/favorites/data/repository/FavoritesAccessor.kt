package com.purecipes.feature.favorites.data.repository

import com.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository

class FavoritesAccessor(
	private val remoteDataSource: FavoritesDataSource.Remote,
) : FavoritesRepository {

	override suspend fun addFavorite(recipeId: Int) = remoteDataSource.addFavorite(recipeId)

	override suspend fun getFavoriteRecipes() = remoteDataSource.getFavoriteRecipes()

	override suspend fun removeFavorite(recipeId: Int) = remoteDataSource.removeFavorite(recipeId)
}

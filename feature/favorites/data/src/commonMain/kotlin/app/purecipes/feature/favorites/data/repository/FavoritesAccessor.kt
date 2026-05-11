package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository

class FavoritesAccessor(
	private val remoteDataSource: FavoritesDataSource.Remote,
) : FavoritesRepository {

	override suspend fun addFavorite(recipeId: Int) = remoteDataSource.addFavorite(recipeId)

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int) =
		remoteDataSource.getFavoriteRecipesPage(pageNumber, pageSize)

	override suspend fun removeFavorite(recipeId: Int) = remoteDataSource.removeFavorite(recipeId)
}

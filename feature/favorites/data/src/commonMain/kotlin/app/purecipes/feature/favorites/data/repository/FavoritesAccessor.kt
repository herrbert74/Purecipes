package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import app.purecipes.feature.favorites.domain.model.FavoriteEvent
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.github.michaelbull.result.getError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FavoritesAccessor(
	private val remoteDataSource: FavoritesDataSource.Remote,
) : FavoritesRepository {

	private val favoriteEvents = MutableSharedFlow<FavoriteEvent>(extraBufferCapacity = 1)

	override suspend fun addFavorite(recipeId: Int) =
		remoteDataSource.addFavorite(recipeId).also { outcome ->
			if (outcome.getError() == null) {
				favoriteEvents.tryEmit(FavoriteEvent.Added(recipeId))
			}
		}

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int) =
		remoteDataSource.getFavoriteRecipesPage(pageNumber, pageSize)

	override suspend fun removeFavorite(recipeId: Int) =
		remoteDataSource.removeFavorite(recipeId).also { outcome ->
			if (outcome.getError() == null) {
				favoriteEvents.tryEmit(FavoriteEvent.Removed(recipeId))
			}
		}

	override fun observeFavoriteEvents(): Flow<FavoriteEvent> = favoriteEvents.asSharedFlow()
}

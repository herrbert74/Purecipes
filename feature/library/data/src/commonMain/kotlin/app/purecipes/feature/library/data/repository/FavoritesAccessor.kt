package app.purecipes.feature.library.data.repository

import app.purecipes.feature.library.data.datasource.FavoritesDataSource
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.repository.FavoritesRepository
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@ContributesBinding(AppScope::class)
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

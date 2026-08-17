package app.purecipes.feature.library.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class FavoritesRemoteDataSource(
	private val api: PurecipesApi,
) : FavoritesDataSource.Remote {

	override suspend fun addFavorite(recipeId: Int): Outcome<Unit> = runCatchingApi {
		api.addFavorite(recipeId)
	}

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int) = runCatchingApi {
		api.getFavoriteRecipesPage(pageNumber = pageNumber, pageSize = pageSize)
	}

	override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = runCatchingApi {
		api.removeFavorite(recipeId)
	}
}

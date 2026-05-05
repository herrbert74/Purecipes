package com.purecipes.feature.favorites.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
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

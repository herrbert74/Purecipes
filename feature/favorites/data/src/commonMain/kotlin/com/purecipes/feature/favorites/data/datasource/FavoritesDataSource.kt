package com.purecipes.feature.favorites.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.SearchResultsPage

interface FavoritesDataSource {

	interface Remote {
		suspend fun addFavorite(recipeId: Int): Outcome<Unit>

		suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage>

		suspend fun removeFavorite(recipeId: Int): Outcome<Unit>
	}
}

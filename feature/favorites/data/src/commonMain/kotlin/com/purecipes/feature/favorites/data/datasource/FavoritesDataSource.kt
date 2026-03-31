package com.purecipes.feature.favorites.data.datasource

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.RecipeSummary

interface FavoritesDataSource {

	interface Remote {
		suspend fun addFavorite(recipeId: Int): Outcome<Unit>

		suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>>

		suspend fun removeFavorite(recipeId: Int): Outcome<Unit>
	}
}

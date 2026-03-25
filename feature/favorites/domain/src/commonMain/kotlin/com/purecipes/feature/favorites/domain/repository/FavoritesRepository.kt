package com.purecipes.feature.favorites.domain.repository

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.shared.domain.model.RecipeSummary

interface FavoritesRepository {

	suspend fun addFavorite(recipeId: Int): Outcome<Unit>

	suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>>

	suspend fun removeFavorite(recipeId: Int): Outcome<Unit>
}

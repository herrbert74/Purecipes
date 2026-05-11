package app.purecipes.feature.favorites.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.SearchResultsPage

interface FavoritesRepository {

	suspend fun addFavorite(recipeId: Int): Outcome<Unit>

	suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage>

	suspend fun removeFavorite(recipeId: Int): Outcome<Unit>
}

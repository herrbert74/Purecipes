package app.purecipes.feature.library.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.SearchResultsPage

interface FavoritesDataSource {

	interface Remote {
		suspend fun addFavorite(recipeId: Int): Outcome<Unit>

		suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage>

		suspend fun removeFavorite(recipeId: Int): Outcome<Unit>
	}
}

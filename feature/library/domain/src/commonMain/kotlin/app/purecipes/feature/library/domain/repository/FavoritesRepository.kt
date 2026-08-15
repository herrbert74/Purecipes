package app.purecipes.feature.library.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.shared.domain.model.SearchResultsPage
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

	suspend fun addFavorite(recipeId: Int): Outcome<Unit>

	suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage>

	suspend fun removeFavorite(recipeId: Int): Outcome<Unit>

	fun observeFavoriteEvents(): Flow<FavoriteEvent>
}

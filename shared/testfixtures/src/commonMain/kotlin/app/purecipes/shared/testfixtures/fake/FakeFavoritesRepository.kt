package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.model.FavoriteEvent
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeFavoritesRepository(
	var getFavoriteRecipesPageResult: Outcome<SearchResultsPage> = Ok(
		SearchResultsPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = 20,
			totalMatches = 0,
		),
	),
	private val addFavoriteResult: Outcome<Unit> = Ok(Unit),
	private val removeFavoriteResult: Outcome<Unit> = Ok(Unit),
) : FavoritesRepository {

	val addedRecipeIds = mutableListOf<Int>()
	val removedRecipeIds = mutableListOf<Int>()

	private val favoriteEvents = MutableSharedFlow<FavoriteEvent>(extraBufferCapacity = 1)

	override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
		addedRecipeIds += recipeId
		return addFavoriteResult.also { outcome ->
			if (outcome.getError() == null) {
				favoriteEvents.tryEmit(FavoriteEvent.Added(recipeId))
			}
		}
	}

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage> =
		getFavoriteRecipesPageResult

	override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> {
		removedRecipeIds += recipeId
		return removeFavoriteResult.also { outcome ->
			if (outcome.getError() == null) {
				favoriteEvents.tryEmit(FavoriteEvent.Removed(recipeId))
			}
		}
	}

	override fun observeFavoriteEvents(): Flow<FavoriteEvent> = favoriteEvents.asSharedFlow()

	fun emitFavoriteEvent(event: FavoriteEvent) {
		favoriteEvents.tryEmit(event)
	}
}

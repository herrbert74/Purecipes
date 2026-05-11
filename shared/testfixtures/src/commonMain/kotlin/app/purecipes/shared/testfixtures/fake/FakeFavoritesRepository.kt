package app.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import app.purecipes.shared.domain.model.SearchResultsPage

class FakeFavoritesRepository(
	private val getFavoriteRecipesPageResult: Outcome<SearchResultsPage> = Ok(
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

	override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
		addedRecipeIds += recipeId
		return addFavoriteResult
	}

	override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage> =
		getFavoriteRecipesPageResult

	override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> {
		removedRecipeIds += recipeId
		return removeFavoriteResult
	}
}

package com.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.shared.domain.model.RecipeSummary

class FakeFavoritesRepository(
	private val getFavoriteRecipesResult: Outcome<List<RecipeSummary>> = Ok(emptyList()),
	private val addFavoriteResult: Outcome<Unit> = Ok(Unit),
	private val removeFavoriteResult: Outcome<Unit> = Ok(Unit),
) : FavoritesRepository {

	val addedRecipeIds = mutableListOf<Int>()
	val removedRecipeIds = mutableListOf<Int>()

	override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
		addedRecipeIds += recipeId
		return addFavoriteResult
	}

	override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = getFavoriteRecipesResult

	override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> {
		removedRecipeIds += recipeId
		return removeFavoriteResult
	}
}

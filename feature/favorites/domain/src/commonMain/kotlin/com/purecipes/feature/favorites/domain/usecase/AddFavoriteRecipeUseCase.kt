package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository

class AddFavoriteRecipeUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<Unit> {
		return repository.addFavorite(recipeId)
	}
}

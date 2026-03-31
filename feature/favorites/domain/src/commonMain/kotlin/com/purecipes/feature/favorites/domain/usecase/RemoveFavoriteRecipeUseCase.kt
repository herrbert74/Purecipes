package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository

class RemoveFavoriteRecipeUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<Unit> {
		return repository.removeFavorite(recipeId)
	}
}

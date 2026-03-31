package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.shared.domain.model.RecipeSummary

class GetFavoriteRecipesUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(): Outcome<List<RecipeSummary>> {
		return repository.getFavoriteRecipes()
	}
}

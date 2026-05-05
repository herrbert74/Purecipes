package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository

class RemoveRecipeFromCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		return repository.removeRecipeFromCookbook(cookbookId, recipeId)
	}
}

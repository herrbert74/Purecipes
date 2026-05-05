package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository

class AddRecipeToCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		return repository.addRecipeToCookbook(cookbookId, recipeId)
	}
}

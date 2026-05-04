package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.shared.domain.model.CookbookRef

class GetRecipeCookbooksUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<List<CookbookRef>> {
		return repository.getRecipeCookbooks(recipeId)
	}
}

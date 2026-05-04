package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository

class DeleteCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int): Outcome<Unit> {
		return repository.deleteCookbook(cookbookId)
	}
}

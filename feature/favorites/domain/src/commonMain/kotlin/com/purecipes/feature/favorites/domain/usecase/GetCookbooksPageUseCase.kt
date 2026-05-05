package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.shared.domain.model.CookbookListPage

class GetCookbooksPageUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> {
		return repository.getCookbooksPage(pageNumber, pageSize)
	}
}

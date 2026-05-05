package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.CookbooksRepository
import com.purecipes.shared.domain.model.SearchResultsPage

class GetCookbookRecipesPageUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> {
		return repository.getCookbookRecipesPage(cookbookId, pageNumber, pageSize)
	}
}

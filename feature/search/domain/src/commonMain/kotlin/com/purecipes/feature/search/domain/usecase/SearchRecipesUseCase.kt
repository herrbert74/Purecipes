package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchResultsPage

class SearchRecipesUseCase(
	private val repository: RecipeSearchRepository,
) {

	suspend operator fun invoke(
		query: String,
		filters: SearchFilters = SearchFilters(),
		pageNumber: Int = 1,
		pageSize: Int = 20,
	): SearchOutcome<SearchResultsPage> {
		return repository.search(query, filters, pageNumber, pageSize)
	}
}

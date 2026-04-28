package com.purecipes.feature.search.domain.repository

import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchResultsPage

interface RecipeSearchRepository {

	suspend fun search(
		query: String,
		filters: SearchFilters = SearchFilters(),
		pageNumber: Int = 1,
		pageSize: Int = 20,
	): SearchOutcome<SearchResultsPage>
}

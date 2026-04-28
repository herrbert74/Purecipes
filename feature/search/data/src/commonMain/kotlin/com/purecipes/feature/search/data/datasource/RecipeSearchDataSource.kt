package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchResultsPage

interface RecipeSearchDataSource {

	interface Remote {
		suspend fun search(
			query: String,
			filters: SearchFilters,
			pageNumber: Int = 1,
			pageSize: Int = 20,
		): SearchOutcome<SearchResultsPage>
	}
}

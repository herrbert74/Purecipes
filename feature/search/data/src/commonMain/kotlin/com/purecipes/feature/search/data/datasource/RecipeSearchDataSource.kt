package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters

interface RecipeSearchDataSource {

	interface Remote {
		suspend fun search(query: String, filters: SearchFilters): SearchOutcome<List<RecipeSummary>>
	}
}

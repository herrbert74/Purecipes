package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.RecipeSummary

interface RecipeSearchDataSource {

	interface Remote {
		suspend fun search(query: String): SearchOutcome<List<RecipeSummary>>
	}
}

package com.purecipes.feature.search.domain.repository

import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters

interface RecipeSearchRepository {

	suspend fun search(query: String, filters: SearchFilters = SearchFilters()): SearchOutcome<List<RecipeSummary>>
}

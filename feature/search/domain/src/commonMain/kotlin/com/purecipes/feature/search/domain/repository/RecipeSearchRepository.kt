package com.purecipes.feature.search.domain.repository

import com.purecipes.feature.search.domain.model.RecipeSummary

interface RecipeSearchRepository {
	suspend fun search(query: String): SearchOutcome<List<RecipeSummary>>
}

package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.SearchFilters

interface RecipeSearchFilterDataSource {

	interface Remote {
		suspend fun getFilters(): SearchOutcome<SearchFilters>
		suspend fun saveFilters(filters: SearchFilters): SearchOutcome<SearchFilters>
	}

	interface Local {
		fun getFilters(): SearchFilters
		fun saveFilters(filters: SearchFilters)
	}
}

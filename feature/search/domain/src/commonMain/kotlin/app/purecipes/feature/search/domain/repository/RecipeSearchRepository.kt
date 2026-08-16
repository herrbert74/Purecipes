package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage

interface RecipeSearchRepository {

	suspend fun search(
		query: String,
		filters: SearchFilters = SearchFilters(),
		keyIngredients: Set<String> = emptySet(),
		pageNumber: Int = 1,
		pageSize: Int = 20,
		applyRecipeFilters: Boolean = true,
	): SearchOutcome<SearchResultsPage>
}

package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage

interface RecipeSearchDataSource {

	interface Remote {

		suspend fun search(
			query: String,
			filters: SearchFilters,
			keyIngredients: Set<String> = emptySet(),
			pageNumber: Int = 1,
			pageSize: Int = 20,
			applyRecipeFilters: Boolean = true,
		): SearchOutcome<SearchResultsPage>
	}
}

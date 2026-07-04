package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.Inject

@Inject
class SearchRecipesUseCase(
	private val repository: RecipeSearchRepository,
) {

	suspend operator fun invoke(
		query: String,
		filters: SearchFilters = SearchFilters(),
		keyIngredients: Set<String> = emptySet(),
		pageNumber: Int = 1,
		pageSize: Int = 20,
	): SearchOutcome<SearchResultsPage> {
		return repository.search(query, filters, keyIngredients, pageNumber, pageSize)
	}
}

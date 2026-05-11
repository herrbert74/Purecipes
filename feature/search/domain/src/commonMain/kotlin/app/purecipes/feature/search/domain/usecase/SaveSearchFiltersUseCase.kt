package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.shared.domain.model.SearchFilters

class SaveSearchFiltersUseCase(
	private val repository: RecipeSearchFilterRepository,
) {

	suspend operator fun invoke(filters: SearchFilters) = repository.saveFilters(filters)
}

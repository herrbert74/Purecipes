package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import com.purecipes.shared.domain.model.SearchFilters

class SaveSearchFiltersUseCase(
	private val repository: RecipeSearchFilterRepository,
) {

	suspend operator fun invoke(filters: SearchFilters) = repository.saveFilters(filters)
}

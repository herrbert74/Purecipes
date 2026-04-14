package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import com.purecipes.shared.domain.model.SearchFilters

class GetSearchFiltersUseCase(
	private val repository: RecipeSearchFilterRepository,
) {

	suspend operator fun invoke(): SearchFilters = repository.getFilters()
}

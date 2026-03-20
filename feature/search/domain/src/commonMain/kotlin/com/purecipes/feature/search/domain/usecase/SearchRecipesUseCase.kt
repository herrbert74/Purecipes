package com.purecipes.feature.search.domain.usecase

import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.RecipeSummary

class SearchRecipesUseCase(
	private val repository: RecipeSearchRepository,
) {

	suspend operator fun invoke(query: String): SearchOutcome<List<RecipeSummary>> {
		return repository.search(query)
	}
}

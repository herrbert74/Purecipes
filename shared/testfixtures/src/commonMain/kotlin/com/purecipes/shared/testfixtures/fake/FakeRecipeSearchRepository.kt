package com.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters

class FakeRecipeSearchRepository(
	private val result: SearchOutcome<List<RecipeSummary>> = Ok(emptyList()),
) : RecipeSearchRepository {

	val queries = mutableListOf<String>()

	override suspend fun search(query: String, filters: SearchFilters): SearchOutcome<List<RecipeSummary>> {
		queries += query
		return result
	}
}

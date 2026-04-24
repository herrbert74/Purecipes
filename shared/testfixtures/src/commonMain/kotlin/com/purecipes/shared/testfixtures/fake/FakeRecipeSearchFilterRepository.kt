package com.purecipes.shared.testfixtures.fake

import com.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import com.purecipes.shared.domain.model.SearchFilters

class FakeRecipeSearchFilterRepository(
	var savedFilters: SearchFilters = SearchFilters(),
) : RecipeSearchFilterRepository {

	override suspend fun getFilters(): SearchFilters = savedFilters

	override suspend fun saveFilters(filters: SearchFilters) {
		savedFilters = filters
	}
}

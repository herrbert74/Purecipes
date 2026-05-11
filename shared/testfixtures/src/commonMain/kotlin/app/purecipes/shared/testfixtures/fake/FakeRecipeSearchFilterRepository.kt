package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.shared.domain.model.SearchFilters

class FakeRecipeSearchFilterRepository(
	var savedFilters: SearchFilters = SearchFilters(),
) : RecipeSearchFilterRepository {

	override suspend fun getFilters(): SearchFilters = savedFilters

	override suspend fun saveFilters(filters: SearchFilters) {
		savedFilters = filters
	}
}

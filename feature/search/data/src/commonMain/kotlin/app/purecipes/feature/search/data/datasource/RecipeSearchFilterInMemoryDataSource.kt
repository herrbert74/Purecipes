package app.purecipes.feature.search.data.datasource

import app.purecipes.shared.domain.model.SearchFilters

class RecipeSearchFilterInMemoryDataSource : RecipeSearchFilterDataSource.Local {

	private var filters = SearchFilters()

	override fun getFilters(): SearchFilters = filters

	override fun saveFilters(filters: SearchFilters) {
		this.filters = filters
	}
}

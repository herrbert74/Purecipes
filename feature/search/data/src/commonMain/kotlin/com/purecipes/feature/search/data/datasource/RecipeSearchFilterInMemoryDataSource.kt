package com.purecipes.feature.search.data.datasource

import com.purecipes.shared.domain.model.SearchFilters

class RecipeSearchFilterInMemoryDataSource : RecipeSearchFilterDataSource.Local {

	private var filters = SearchFilters()

	override fun getFilters(): SearchFilters = filters

	override fun saveFilters(filters: SearchFilters) {
		this.filters = filters
	}
}

package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.SearchFilters

class RecipeSearchFilterRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeSearchFilterDataSource.Remote {

	override suspend fun getFilters(): SearchOutcome<SearchFilters> = runCatchingApi {
		api.getSearchFilters()
	}

	override suspend fun saveFilters(filters: SearchFilters): SearchOutcome<SearchFilters> = runCatchingApi {
		api.saveSearchFilters(filters)
	}
}

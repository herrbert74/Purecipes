package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchRequest
import com.purecipes.shared.domain.model.SearchResultsPage

class RecipeSearchRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeSearchDataSource.Remote {

	override suspend fun search(
		query: String,
		filters: SearchFilters,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> {
		return runCatchingApi {
			api.searchWithFilters(
				SearchRequest(
					query = query.trim(),
					filters = filters,
					pageNumber = pageNumber,
					pageSize = pageSize,
				),
			)
		}
	}
}

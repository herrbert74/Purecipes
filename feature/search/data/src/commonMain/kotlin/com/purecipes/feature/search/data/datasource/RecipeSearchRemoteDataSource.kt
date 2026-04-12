package com.purecipes.feature.search.data.datasource

import com.purecipes.feature.search.domain.repository.SearchOutcome
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.util.runCatchingApi
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.domain.model.SearchRequest

class RecipeSearchRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeSearchDataSource.Remote {

	override suspend fun search(query: String, filters: SearchFilters): SearchOutcome<List<RecipeSummary>> {
		return runCatchingApi {
			api.searchWithFilters(SearchRequest(query = query.trim(), filters = filters))
		}
	}
}

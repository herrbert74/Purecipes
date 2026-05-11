package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.SearchFilters

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

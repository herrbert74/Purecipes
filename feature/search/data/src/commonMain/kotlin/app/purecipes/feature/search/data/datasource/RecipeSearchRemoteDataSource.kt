package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchRequest
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeSearchRemoteDataSource(
	private val api: PurecipesApi,
) : RecipeSearchDataSource.Remote {

	override suspend fun search(
		query: String,
		filters: SearchFilters,
		keyIngredients: Set<String>,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> {
		return runCatchingApi {
			api.searchWithFilters(
				SearchRequest(
					query = query.trim(),
					filters = filters,
					keyIngredients = keyIngredients,
					pageNumber = pageNumber,
					pageSize = pageSize,
				),
			)
		}
	}
}

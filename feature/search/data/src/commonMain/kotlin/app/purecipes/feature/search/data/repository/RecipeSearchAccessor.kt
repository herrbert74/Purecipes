package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.RecipeSearchDataSource
import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeSearchAccessor(
	private val remoteDataSource: RecipeSearchDataSource.Remote,
) : RecipeSearchRepository {

	override suspend fun search(
		query: String,
		filters: SearchFilters,
		keyIngredients: Set<String>,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> =
		remoteDataSource.search(query, filters, keyIngredients, pageNumber, pageSize)
}

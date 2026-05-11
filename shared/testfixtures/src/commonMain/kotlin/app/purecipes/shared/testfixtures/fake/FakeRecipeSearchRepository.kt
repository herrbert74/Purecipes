package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError

class FakeRecipeSearchRepository(
	private val result: SearchOutcome<List<RecipeSummary>> = Ok(emptyList()),
	private val totalMatches: Int? = null,
) : RecipeSearchRepository {

	val queries = mutableListOf<String>()
	var lastQuery: String? = null
	var lastPageNumber: Int? = null
	var lastPageSize: Int? = null

	override suspend fun search(
		query: String,
		filters: SearchFilters,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> {
		lastQuery = query
		lastPageNumber = pageNumber
		lastPageSize = pageSize
		queries += query
		val error = result.getError()
		if (error != null) {
			return Err(error)
		}
		val items = result.get() ?: emptyList()
		return Ok(
			SearchResultsPage(
				items = items,
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = totalMatches ?: items.size,
			),
		)
	}
}

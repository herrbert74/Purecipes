package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.NearMissRecipe
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
	private val nearMissRecipes: List<NearMissRecipe> = emptyList(),
) : RecipeSearchRepository {

	val queries = mutableListOf<String>()
	var lastQuery: String? = null
	var lastKeyIngredients: Set<String>? = null
	var lastPageNumber: Int? = null
	var lastPageSize: Int? = null
	var lastFilters: SearchFilters? = null
	var lastApplyRecipeFilters: Boolean? = null

	override suspend fun search(
		query: String,
		filters: SearchFilters,
		keyIngredients: Set<String>,
		pageNumber: Int,
		pageSize: Int,
		applyRecipeFilters: Boolean,
	): SearchOutcome<SearchResultsPage> {
		lastQuery = query
		lastFilters = filters
		lastKeyIngredients = keyIngredients
		lastPageNumber = pageNumber
		lastPageSize = pageSize
		lastApplyRecipeFilters = applyRecipeFilters
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
				nearMissRecipes = nearMissRecipes,
			),
		)
	}
}

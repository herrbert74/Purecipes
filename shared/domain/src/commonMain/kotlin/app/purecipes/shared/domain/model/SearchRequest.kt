package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
	val query: String,
	val filters: SearchFilters = SearchFilters(),
	val keyIngredients: Set<String> = emptySet(),
	val pageNumber: Int = 1,
	val pageSize: Int = 20,
)

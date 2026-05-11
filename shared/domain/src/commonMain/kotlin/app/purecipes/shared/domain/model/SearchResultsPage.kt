package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResultsPage(
	val items: List<RecipeSummary>,
	val pageNumber: Int,
	val pageSize: Int,
	val totalMatches: Int,
)

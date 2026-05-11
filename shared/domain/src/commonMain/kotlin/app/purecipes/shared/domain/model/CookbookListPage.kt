package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CookbookListPage(
	val items: List<CookbookSummary>,
	val pageNumber: Int,
	val pageSize: Int,
	val totalMatches: Int,
)

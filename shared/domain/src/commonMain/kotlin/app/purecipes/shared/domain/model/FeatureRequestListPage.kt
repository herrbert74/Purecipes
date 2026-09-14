package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureRequestListPage(
	val items: List<FeatureRequest>,
	val pageNumber: Int,
	val pageSize: Int,
	val totalMatches: Int,
)

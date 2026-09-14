package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureRequestCreateRequest(
	val title: String,
	val description: String,
)

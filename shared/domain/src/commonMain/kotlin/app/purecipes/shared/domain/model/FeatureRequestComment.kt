package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureRequestComment(
	val id: Int,
	val requestId: Int,
	val authorDisplayName: String,
	val body: String,
	val createdAtEpochMillis: Long,
)

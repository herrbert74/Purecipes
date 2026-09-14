package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureRequest(
	val id: Int,
	val title: String,
	val description: String,
	val status: FeatureRequestStatus,
	val voteCount: Int,
	val commentCount: Int,
	val createdAtEpochMillis: Long,
	val votedByCurrentUser: Boolean,
)

package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FeatureRequestStatus {
	OPEN,
	PLANNED,
	IN_PROGRESS,
	DONE,
}

fun featureRequestStatusFromRawValue(rawValue: String?): FeatureRequestStatus {
	val normalized = rawValue?.trim()?.uppercase()
	return FeatureRequestStatus.entries.firstOrNull { it.name == normalized } ?: FeatureRequestStatus.OPEN
}

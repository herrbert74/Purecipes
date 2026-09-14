package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FeatureRequestSort {
	TOP_VOTES,
	NEWEST,
}

fun featureRequestSortFromRawValue(rawValue: String?): FeatureRequestSort {
	val normalized = rawValue?.trim()?.uppercase()
	return FeatureRequestSort.entries.firstOrNull { it.name == normalized } ?: FeatureRequestSort.TOP_VOTES
}

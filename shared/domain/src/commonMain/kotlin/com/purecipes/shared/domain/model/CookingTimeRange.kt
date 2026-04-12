package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CookingTimeRange(val displayName: String) {
	UNDER_15("Under 15 min"),
	UNDER_30("Under 30 min"),
	UNDER_60("Under 1 hr"),
	OVER_60("Over 1 hr"),
}

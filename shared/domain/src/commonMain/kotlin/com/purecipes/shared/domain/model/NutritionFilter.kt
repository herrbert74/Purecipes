package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class NutritionFilter(val displayName: String) {
	LOW_CARB("Low Carb"),
	HIGH_PROTEIN("High Protein"),
	LOW_SODIUM("Low Sodium"),
	LOW_FAT("Low Fat"),
	HIGH_FIBER("High Fiber"),
}

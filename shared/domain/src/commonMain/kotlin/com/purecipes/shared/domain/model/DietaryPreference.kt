package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class DietaryPreference(val displayName: String) {
	VEGAN("Vegan"),
	VEGETARIAN("Vegetarian"),
	GLUTEN_FREE("Gluten-Free"),
	DAIRY_FREE("Dairy-Free"),
	NUT_FREE("Nut-Free"),
	EGG_FREE("Egg-Free"),
	HALAL("Halal"),
	KOSHER("Kosher"),
	LOW_FODMAP("Low FODMAP"),
	PALEO("Paleo"),
	KETO("Keto"),
}

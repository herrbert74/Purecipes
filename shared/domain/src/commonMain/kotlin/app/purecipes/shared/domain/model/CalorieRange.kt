package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CalorieRange(val displayName: String) {
	LOW("Under 300 kcal"),
	MEDIUM("300–600 kcal"),
	HIGH("Over 600 kcal"),
}

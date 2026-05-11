package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementSystem {
	IMPERIAL,
	METRIC,
	MIXED,
}

@Serializable
enum class RecipeFormatHandling {
	KEEP_AS_IS,
	FILTER_OUT,
	CONVERT_TO_PREFERRED,
}

@Serializable
data class MeasurementPreferences(
	val preferredSystem: MeasurementSystem,
	val formatHandling: RecipeFormatHandling = RecipeFormatHandling.KEEP_AS_IS,
	val detectedCountryCode: String? = null,
	val notificationSeenRecipeIds: Set<Int> = emptySet(),
)

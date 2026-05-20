package app.purecipes.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NutritionConfidence {
	@SerialName("complete")
	COMPLETE,

	@SerialName("partial")
	PARTIAL,
}

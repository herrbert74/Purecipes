package app.purecipes.shared.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NutritionCalculationSource {
	@SerialName("calculated")
	CALCULATED,

	@SerialName("scraped")
	SCRAPED,
}

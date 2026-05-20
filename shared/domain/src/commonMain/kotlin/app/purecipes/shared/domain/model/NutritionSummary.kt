package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NutritionSummary(
	val calories: Double? = null,
	val protein: Double? = null,
	val carbohydrates: Double? = null,
	val fat: Double? = null,
	val fiber: Double? = null,
	val sugar: Double? = null,
	val sodium: Double? = null,
	val matchedIngredientCount: Int? = null,
	val totalIngredientCount: Int? = null,
	val calculationSource: NutritionCalculationSource? = null,
	val confidence: NutritionConfidence? = null,
	val isComplete: Boolean = false,
)

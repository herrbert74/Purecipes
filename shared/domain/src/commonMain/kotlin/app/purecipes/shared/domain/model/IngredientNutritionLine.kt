package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientNutritionLine(
	val ingredientId: Int,
	val rawText: String,
	val parsedName: String? = null,
	val quantity: Double? = null,
	val unit: String? = null,
	val isMeasurable: Boolean = false,
	val isMatched: Boolean = false,
	val foodDisplayName: String? = null,
	val grams: Double? = null,
	val contribution: NutritionSummary = NutritionSummary(),
	val per100Grams: NutritionSummary? = null,
)

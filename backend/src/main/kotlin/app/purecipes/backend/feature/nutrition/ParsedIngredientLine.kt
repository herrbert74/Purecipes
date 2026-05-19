package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class ParsedIngredientLine(
	val rawText: String,
	val quantity: BigDecimal?,
	val unit: String?,
	val parsedName: String,
	val isMeasurable: Boolean,
)

internal data class IngredientNutritionContribution(
	val foodId: Int,
	val grams: BigDecimal,
	val matchSource: String,
	val confidence: BigDecimal,
)

internal data class CalculatedNutritionTotals(
	val calories: BigDecimal,
	val protein: BigDecimal?,
	val carbohydrates: BigDecimal?,
	val fat: BigDecimal?,
	val fiber: BigDecimal?,
	val sugar: BigDecimal?,
	val sodium: BigDecimal?,
	val matchedIngredientCount: Int,
	val totalIngredientCount: Int,
	val isComplete: Boolean,
)

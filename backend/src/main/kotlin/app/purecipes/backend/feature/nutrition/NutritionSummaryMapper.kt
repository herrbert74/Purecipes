package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionConfidence
import app.purecipes.shared.domain.model.NutritionSummary
import java.math.BigDecimal

internal fun CalculatedNutritionTotals.toNutritionSummary(
	calculationSource: NutritionCalculationSource = NutritionCalculationSource.CALCULATED,
): NutritionSummary {
	val confidence = when {
		isComplete -> NutritionConfidence.COMPLETE
		matchedIngredientCount > 0 -> NutritionConfidence.PARTIAL
		else -> null
	}

	return NutritionSummary(
		calories = calories.toDouble(),
		protein = protein.toDoubleOrNull(),
		carbohydrates = carbohydrates.toDoubleOrNull(),
		fat = fat.toDoubleOrNull(),
		fiber = fiber.toDoubleOrNull(),
		sugar = sugar.toDoubleOrNull(),
		sodium = sodium.toDoubleOrNull(),
		matchedIngredientCount = matchedIngredientCount,
		totalIngredientCount = totalIngredientCount,
		calculationSource = calculationSource,
		confidence = confidence,
		isComplete = isComplete,
	)
}

internal fun RecipeNutritionCalculationResult.toNutritionSummary(): NutritionSummary? {
	val countableResults = ingredientResults.filter { result ->
		!IngredientVocabulary.isIgnorableIngredientLine(result.parsed.rawText)
	}
	if (countableResults.isEmpty()) {
		return null
	}

	val totals = totals
	return if (totals != null) {
		totals.toNutritionSummary()
	} else {
		val matchedCount = countableResults.count { result ->
			result.foodMatch != null && result.grams != null
		}
		NutritionSummary(
			matchedIngredientCount = matchedCount,
			totalIngredientCount = countableResults.size,
			calculationSource = NutritionCalculationSource.CALCULATED,
			confidence = if (matchedCount > 0) NutritionConfidence.PARTIAL else null,
			isComplete = false,
		)
	}
}

private fun BigDecimal?.toDoubleOrNull(): Double? = this?.toDouble()

package app.purecipes.backend.feature.nutrition

import app.purecipes.shared.domain.model.NutritionSummary
import java.math.BigDecimal
import java.math.RoundingMode

internal fun NutritionSummary.scale(factor: Double): NutritionSummary {
	if (factor <= 0.0 || !factor.isFinite()) {
		return NutritionSummary(
			matchedIngredientCount = matchedIngredientCount,
			totalIngredientCount = totalIngredientCount,
			calculationSource = calculationSource,
			confidence = confidence,
			isComplete = isComplete,
		)
	}

	val multiplier = BigDecimal.valueOf(factor)
	return NutritionSummary(
		calories = calories?.let { value -> scaleValue(value, multiplier) },
		protein = protein?.let { value -> scaleValue(value, multiplier) },
		carbohydrates = carbohydrates?.let { value -> scaleValue(value, multiplier) },
		fat = fat?.let { value -> scaleValue(value, multiplier) },
		fiber = fiber?.let { value -> scaleValue(value, multiplier) },
		sugar = sugar?.let { value -> scaleValue(value, multiplier) },
		sodium = sodium?.let { value -> scaleValue(value, multiplier) },
		matchedIngredientCount = matchedIngredientCount,
		totalIngredientCount = totalIngredientCount,
		calculationSource = calculationSource,
		confidence = confidence,
		isComplete = isComplete,
	)
}

internal fun computeStoredIngredientNutrition(
	food: NutritionFoodRecord,
	grams: BigDecimal,
): StoredIngredientNutrition {
	val factor = grams.divide(GRAMS_PER_100, NUTRIENT_SCALE, RoundingMode.HALF_UP)
	val nutrients = food.nutrients
	return StoredIngredientNutrition(
		grams = grams.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		calories = nutrients.calories.multiply(factor).setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		protein = nutrients.protein?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		carbohydrates = nutrients.carbohydrates?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		fat = nutrients.fat?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		fiber = nutrients.fiber?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		sugar = nutrients.sugar?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
		sodium = nutrients.sodium?.multiply(factor)?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
	)
}

internal fun computeNutrientsForGrams(
	food: NutritionFoodRecord,
	grams: BigDecimal,
): NutritionSummary {
	val factor = grams.divide(GRAMS_PER_100, NUTRIENT_SCALE, RoundingMode.HALF_UP)
	val nutrients = food.nutrients
	return NutritionSummary(
		calories = nutrients.calories.multiply(factor).toDoubleScaled(),
		protein = nutrients.protein?.multiply(factor)?.toDoubleScaled(),
		carbohydrates = nutrients.carbohydrates?.multiply(factor)?.toDoubleScaled(),
		fat = nutrients.fat?.multiply(factor)?.toDoubleScaled(),
		fiber = nutrients.fiber?.multiply(factor)?.toDoubleScaled(),
		sugar = nutrients.sugar?.multiply(factor)?.toDoubleScaled(),
		sodium = nutrients.sodium?.multiply(factor)?.toDoubleScaled(),
	)
}

internal fun NutritionFoodRecord.nutrientsPer100Grams(): NutritionSummary {
	val nutrients = nutrients
	return NutritionSummary(
		calories = nutrients.calories.toDouble(),
		protein = nutrients.protein.toNutrientDoubleOrNull(),
		carbohydrates = nutrients.carbohydrates.toNutrientDoubleOrNull(),
		fat = nutrients.fat.toNutrientDoubleOrNull(),
		fiber = nutrients.fiber.toNutrientDoubleOrNull(),
		sugar = nutrients.sugar.toNutrientDoubleOrNull(),
		sodium = nutrients.sodium.toNutrientDoubleOrNull(),
	)
}

private fun scaleValue(value: Double, multiplier: BigDecimal): Double =
	BigDecimal.valueOf(value).multiply(multiplier).setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP).toDouble()

private fun BigDecimal?.toNutrientDoubleOrNull(): Double? = this?.toDouble()

private fun BigDecimal.toDoubleScaled(): Double =
	setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP).toDouble()

private val GRAMS_PER_100 = BigDecimal("100")
private const val NUTRIENT_SCALE = 2

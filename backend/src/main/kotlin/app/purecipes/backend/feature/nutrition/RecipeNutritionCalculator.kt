package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.math.RoundingMode

internal data class RecipeNutritionCalculationResult(
	val totals: CalculatedNutritionTotals?,
	val ingredientResults: List<IngredientNutritionCalculationResult>,
)

internal data class IngredientNutritionCalculationResult(
	val ingredientId: Int,
	val parsed: ParsedIngredientLine,
	val foodMatch: NutritionFoodMatch?,
	val grams: BigDecimal?,
)

internal class RecipeNutritionCalculator(
	private val lookupIndex: NutritionLookupIndex,
) {
	fun calculate(ingredients: List<RecipeIngredientRow>): RecipeNutritionCalculationResult {
		val ingredientResults = ingredients.map { ingredient ->
			calculateIngredient(ingredient)
		}

		val countableResults = ingredientResults.filter { result ->
			!IngredientVocabulary.isIgnorableIngredientLine(result.parsed.rawText)
		}
		if (countableResults.isEmpty()) {
			return RecipeNutritionCalculationResult(totals = null, ingredientResults = ingredientResults)
		}

		var calories = BigDecimal.ZERO
		var protein: BigDecimal? = null
		var carbohydrates: BigDecimal? = null
		var fat: BigDecimal? = null
		var fiber: BigDecimal? = null
		var sugar: BigDecimal? = null
		var sodium: BigDecimal? = null
		var matchedCount = 0

		countableResults.forEach { result ->
			val food = result.foodMatch?.foodId?.let(lookupIndex::food) ?: return@forEach
			val grams = result.grams ?: return@forEach
			matchedCount++
			val factor = grams.divide(GRAMS_PER_100, NUTRIENT_SCALE, RoundingMode.HALF_UP)
			val nutrients = food.nutrients
			calories = calories.add(nutrients.calories.multiply(factor))
			protein = addNullable(protein, nutrients.protein, factor)
			carbohydrates = addNullable(carbohydrates, nutrients.carbohydrates, factor)
			fat = addNullable(fat, nutrients.fat, factor)
			fiber = addNullable(fiber, nutrients.fiber, factor)
			sugar = addNullable(sugar, nutrients.sugar, factor)
			sodium = addNullable(sodium, nutrients.sodium, factor)
		}

		val totalCount = countableResults.size
		val allMeasurableMatched = countableResults.all { result ->
			result.parsed.isMeasurable && result.foodMatch != null && result.grams != null
		}
		val totals = CalculatedNutritionTotals(
			calories = calories.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			protein = protein?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			carbohydrates = carbohydrates?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			fat = fat?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			fiber = fiber?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			sugar = sugar?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			sodium = sodium?.setScale(NUTRIENT_SCALE, RoundingMode.HALF_UP),
			matchedIngredientCount = matchedCount,
			totalIngredientCount = totalCount,
			isComplete = matchedCount == totalCount && totalCount > 0 && allMeasurableMatched,
		)

		return RecipeNutritionCalculationResult(
			totals = totals,
			ingredientResults = ingredientResults,
		)
	}

	private fun calculateIngredient(ingredient: RecipeIngredientRow): IngredientNutritionCalculationResult {
		val parsed = IngredientLineParser.parse(ingredient.rawText)
		if (!parsed.isMeasurable) {
			return IngredientNutritionCalculationResult(
				ingredientId = ingredient.ingredientId,
				parsed = parsed,
				foodMatch = null,
				grams = null,
			)
		}

		val foodMatch = lookupIndex.findFood(parsed.parsedName)
		val quantity = parsed.quantity
		val unit = parsed.unit
		if (foodMatch == null || quantity == null || unit == null) {
			return IngredientNutritionCalculationResult(
				ingredientId = ingredient.ingredientId,
				parsed = parsed,
				foodMatch = foodMatch,
				grams = null,
			)
		}

		val grams = IngredientGramWeightResolver.resolveGrams(
			quantity = quantity,
			unit = unit,
			foodMeasures = lookupIndex.measuresForFood(foodMatch.foodId),
		)
		return IngredientNutritionCalculationResult(
			ingredientId = ingredient.ingredientId,
			parsed = parsed,
			foodMatch = foodMatch,
			grams = grams,
		)
	}

	private fun addNullable(
		current: BigDecimal?,
		value: BigDecimal?,
		factor: BigDecimal,
	): BigDecimal? {
		if (value == null) {
			return current
		}
		val contribution = value.multiply(factor)
		return current?.add(contribution) ?: contribution
	}

	private companion object {
		val GRAMS_PER_100 = BigDecimal("100")
		const val NUTRIENT_SCALE = 2
	}
}

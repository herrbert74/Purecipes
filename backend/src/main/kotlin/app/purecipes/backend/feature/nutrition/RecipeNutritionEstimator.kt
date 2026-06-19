package app.purecipes.backend.feature.nutrition

import app.purecipes.shared.domain.model.NutritionSummary
import javax.sql.DataSource

internal class RecipeNutritionEstimator(
	private val lookupRepository: NutritionLookupRepository,
) {
	constructor(dataSource: DataSource) : this(NutritionLookupRepository(dataSource))

	fun estimate(ingredients: List<String>): NutritionSummary? {
		val normalizedIngredients = ingredients.map(String::trim).filter(String::isNotEmpty)
		if (normalizedIngredients.isEmpty()) {
			return null
		}

		val ingredientRows = normalizedIngredients.mapIndexed { index, rawText ->
			RecipeIngredientRow(
				ingredientId = index,
				rawText = rawText,
				requirement = "REQUIRED",
				alternativeGroupKey = null,
			)
		}
		val calculation = RecipeNutritionCalculator(lookupRepository.loadIndex()).calculate(ingredientRows)
		return calculation.toNutritionSummary()
	}
}

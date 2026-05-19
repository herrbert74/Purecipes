package app.purecipes.backend.feature.nutrition

import javax.sql.DataSource

internal data class RecipeNutritionPersistResult(
	val recipeId: Int,
	val totals: CalculatedNutritionTotals?,
	val ingredientCount: Int,
)

internal class RecipeNutritionService(
	private val recipeNutritionRepository: RecipeNutritionRepository,
	private val lookupRepository: NutritionLookupRepository,
) {
	constructor(dataSource: DataSource) : this(
		recipeNutritionRepository = RecipeNutritionRepository(dataSource),
		lookupRepository = NutritionLookupRepository(dataSource),
	)

	fun calculateAndPersist(recipeId: Int): RecipeNutritionPersistResult {
		val ingredients = recipeNutritionRepository.loadRecipeIngredients(recipeId)
		val calculation = RecipeNutritionCalculator(lookupRepository.loadIndex()).calculate(ingredients)
		persist(recipeId, calculation)
		return RecipeNutritionPersistResult(
			recipeId = recipeId,
			totals = calculation.totals,
			ingredientCount = ingredients.size,
		)
	}

	fun calculateAndPersistAll(): List<RecipeNutritionPersistResult> =
		recipeNutritionRepository.listRecipeIds().map(::calculateAndPersist)

	private fun persist(recipeId: Int, calculation: RecipeNutritionCalculationResult) {
		calculation.ingredientResults.forEach { result ->
			recipeNutritionRepository.upsertIngredientMeasurement(
				ingredientId = result.ingredientId,
				parsed = result.parsed,
			)
			val foodMatch = result.foodMatch
			if (foodMatch != null) {
				recipeNutritionRepository.upsertIngredientMatch(
					ingredientId = result.ingredientId,
					parsed = result.parsed,
					foodMatch = foodMatch,
				)
			} else {
				recipeNutritionRepository.deleteIngredientMatch(result.ingredientId)
			}
		}

		val totals = calculation.totals
		if (totals != null && totals.matchedIngredientCount > 0) {
			recipeNutritionRepository.upsertRecipeNutrition(recipeId, totals)
		}
	}
}

package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import javax.sql.DataSource

internal data class RecipeNutritionPersistResult(
	val recipeId: Int,
	val totals: CalculatedNutritionTotals?,
	val ingredientCount: Int,
	val issues: List<IngredientNutritionIssue>,
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
		val lookupIndex = lookupRepository.loadIndex()
		val calculation = RecipeNutritionCalculator(lookupIndex).calculate(ingredients)
		persist(recipeId, calculation, lookupIndex)
		return RecipeNutritionPersistResult(
			recipeId = recipeId,
			totals = calculation.totals,
			ingredientCount = ingredients.size,
			issues = calculation.collectIngredientIssues(),
		)
	}

	fun calculateAndPersistAll(): List<RecipeNutritionPersistResult> =
		recipeNutritionRepository.listRecipeIds().map(::calculateAndPersist)

	fun calculateAndPersistRecipeIds(recipeIds: List<Int>): List<RecipeNutritionPersistResult> =
		recipeIds.map(::calculateAndPersist)

	private fun persist(
		recipeId: Int,
		calculation: RecipeNutritionCalculationResult,
		lookupIndex: NutritionLookupIndex,
	) {
		var totalWeightGrams = BigDecimal.ZERO

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

			val grams = result.grams
			val food = foodMatch?.foodId?.let(lookupIndex::food)
			if (food != null && grams != null) {
				totalWeightGrams = totalWeightGrams.add(grams)
				recipeNutritionRepository.upsertIngredientContribution(
					ingredientId = result.ingredientId,
					contribution = computeStoredIngredientNutrition(food, grams),
				)
			} else {
				recipeNutritionRepository.deleteIngredientContribution(result.ingredientId)
			}
		}

		val totals = calculation.totals
		if (totals != null && totals.matchedIngredientCount > 0) {
			val yields = recipeNutritionRepository.loadRecipeYields(recipeId)
			val servingCount = ServingsParser.parse(yields)?.count?.let(BigDecimal::valueOf)
			recipeNutritionRepository.upsertRecipeNutrition(
				recipeId = recipeId,
				totals = totals,
				totalWeightGrams = totalWeightGrams.takeIf { weight -> weight > BigDecimal.ZERO },
				servingCount = servingCount,
			)
		}
	}
}

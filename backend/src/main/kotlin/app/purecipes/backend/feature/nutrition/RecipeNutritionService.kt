package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import javax.sql.DataSource

internal data class RecipeNutritionPersistResult(
	val recipeId: Int,
	val totals: CalculatedNutritionTotals?,
	val ingredientCount: Int,
	val issues: List<IngredientNutritionIssue>,
	val allIssues: List<IngredientNutritionIssue> = issues,
)

private const val SCRAPED_CALCULATION_SOURCE = "scraped"

internal class RecipeNutritionService(
	private val recipeNutritionRepository: RecipeNutritionRepository,
	private val lookupRepository: NutritionLookupRepository,
	private val lineOverridesProvider: () -> Map<IngredientLineOverrideKey, IngredientLineOverride> =
		IngredientLineOverridesLoader::loadDefaultOverrides,
) {

	constructor(dataSource: DataSource) : this(
		recipeNutritionRepository = RecipeNutritionRepository(dataSource),
		lookupRepository = NutritionLookupRepository(
			dataSource = dataSource,
			foodPinsProvider = IngredientPinsLoader::loadDefaultFoodPins,
		),
	)

	fun calculateAndPersist(
		recipeId: Int,
		lookupIndex: NutritionLookupIndex = lookupRepository.loadIndex(),
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride> = lineOverridesProvider(),
	): RecipeNutritionPersistResult {
		val ingredients = recipeNutritionRepository.loadRecipeIngredients(recipeId)
		val calculation = RecipeNutritionCalculator(lookupIndex, lineOverrides).calculate(ingredients)
		persist(recipeId, calculation, lookupIndex)
		return RecipeNutritionPersistResult(
			recipeId = recipeId,
			totals = calculation.totals,
			ingredientCount = ingredients.size,
			issues = calculation.collectIngredientIssues(onlyCountableTowardsTotals = true),
			allIssues = calculation.collectIngredientIssues(onlyCountableTowardsTotals = false),
		)
	}

	fun calculateAndPersistAll(
		onProgress: (current: Int, total: Int) -> Unit = { _, _ -> },
	): List<RecipeNutritionPersistResult> =
		calculateAndPersistRecipeIds(recipeNutritionRepository.listRecipeIds(), onProgress)

	fun calculateAndPersistRecipeIds(
		recipeIds: List<Int>,
		onProgress: (current: Int, total: Int) -> Unit = { _, _ -> },
	): List<RecipeNutritionPersistResult> {
		if (recipeIds.isEmpty()) {
			return emptyList()
		}
		val lookupIndex = lookupRepository.loadIndex()
		val lineOverrides = lineOverridesProvider()
		val total = recipeIds.size
		return recipeIds.mapIndexed { index, recipeId ->
			val result = calculateAndPersist(recipeId, lookupIndex, lineOverrides)
			onProgress(index + 1, total)
			result
		}
	}

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
			val gramsSource = result.gramsSource
			val food = foodMatch?.foodId?.let(lookupIndex::food)
			if (food != null && grams != null && gramsSource != null) {
				totalWeightGrams = totalWeightGrams.add(grams)
				recipeNutritionRepository.upsertIngredientContribution(
					ingredientId = result.ingredientId,
					contribution = computeStoredIngredientNutrition(food, grams, gramsSource),
				)
			} else {
				recipeNutritionRepository.deleteIngredientContribution(result.ingredientId)
			}
		}

		if (recipeNutritionRepository.loadNutritionCalculationSource(recipeId) == SCRAPED_CALCULATION_SOURCE) {
			return
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

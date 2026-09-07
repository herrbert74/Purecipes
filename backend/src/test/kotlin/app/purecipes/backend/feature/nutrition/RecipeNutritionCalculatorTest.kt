package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import kotlin.test.Test

class RecipeNutritionCalculatorTest {

	@Test
	fun calculateTotalsForMatchedIngredient() {
		val lookupIndex = NutritionLookupIndex(
			foodById = mapOf(
				1 to NutritionFoodRecord(
					id = 1,
					displayName = "Sugars, granulated",
					normalizedName = "sugars granulated",
					nutrients = FdcNutrientsPer100g(
						calories = BigDecimal("387"),
						protein = BigDecimal.ZERO,
						carbohydrates = BigDecimal("99.98"),
						fat = BigDecimal.ZERO,
						fiber = BigDecimal.ZERO,
						sugar = BigDecimal("99.8"),
						sodium = BigDecimal.ONE,
					),
				),
			),
			foodIdByNormalizedAlias = mapOf("sugar" to 1),
			measuresByFoodId = mapOf(1 to mapOf("cup" to BigDecimal("188"))),
		)
		val calculator = RecipeNutritionCalculator(lookupIndex)
		val result = calculator.calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 10, rawText = "1 cup sugar"),
			),
		)

		result.totals shouldNotBe null
		result.totals?.matchedIngredientCount shouldBe 1
		result.totals?.totalIngredientCount shouldBe 1
		result.totals?.isComplete shouldBe true
		result.totals?.calories shouldBe BigDecimal("727.56")
		result.ingredientResults.single().gramsSource shouldBe GramWeightSource.MEASURE
	}

	@Test
	fun calculateUsesGarlicCloveMeasureForBareCloveCounts() {
		val lookupIndex = NutritionLookupIndex(
			foodById = mapOf(
				4 to NutritionFoodRecord(
					id = 4,
					displayName = "Garlic, raw",
					normalizedName = "garlic raw",
					nutrients = FdcNutrientsPer100g(
						calories = BigDecimal("149"),
						protein = BigDecimal("6.36"),
						carbohydrates = BigDecimal("33.06"),
						fat = BigDecimal("0.5"),
						fiber = BigDecimal("2.1"),
						sugar = BigDecimal("1"),
						sodium = BigDecimal("17"),
					),
				),
			),
			foodIdByNormalizedAlias = mapOf("garlic" to 4),
			measuresByFoodId = mapOf(4 to mapOf("clove" to BigDecimal("3"))),
		)
		val calculator = RecipeNutritionCalculator(lookupIndex)
		val result = calculator.calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 11, rawText = "8 cloves"),
			),
		)

		result.ingredientResults.single().foodMatch?.foodId shouldBe 4
		result.ingredientResults.single().grams shouldBe BigDecimal("24")
		result.ingredientResults.single().gramsSource shouldBe GramWeightSource.MEASURE
	}

	@Test
	fun calculateDoesNotUseGarlicGramsForGroundClove() {
		val lookupIndex = NutritionLookupIndex(
			foodById = mapOf(
				4 to NutritionFoodRecord(
					id = 4,
					displayName = "Garlic, raw",
					normalizedName = "garlic raw",
					nutrients = FdcNutrientsPer100g(
						calories = BigDecimal("149"),
						protein = null,
						carbohydrates = null,
						fat = null,
						fiber = null,
						sugar = null,
						sodium = null,
					),
				),
				9 to NutritionFoodRecord(
					id = 9,
					displayName = "Spices, cloves, ground",
					normalizedName = "spices cloves ground",
					nutrients = FdcNutrientsPer100g(
						calories = BigDecimal("274"),
						protein = null,
						carbohydrates = null,
						fat = null,
						fiber = null,
						sugar = null,
						sodium = null,
					),
				),
			),
			foodIdByNormalizedAlias = mapOf(
				"garlic" to 4,
				"ground clove" to 9,
			),
			measuresByFoodId = mapOf(4 to mapOf("clove" to BigDecimal("3"))),
		)
		val calculator = RecipeNutritionCalculator(lookupIndex)
		val result = calculator.calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 12, rawText = "1 clove, ground"),
			),
		)

		result.ingredientResults.single().foodMatch?.foodId shouldBe 9
		result.ingredientResults.single().grams shouldBe null
	}

	@Test
	fun calculateUsesPackTimesMassForCannedProduce() {
		val lookupIndex = NutritionLookupIndex(
			foodById = mapOf(
				7 to NutritionFoodRecord(
					id = 7,
					displayName = "Tomatoes, red, ripe, raw, year round average",
					normalizedName = "tomatoes red ripe raw",
					nutrients = FdcNutrientsPer100g(
						calories = BigDecimal("18"),
						protein = null,
						carbohydrates = null,
						fat = null,
						fiber = null,
						sugar = null,
						sodium = null,
					),
				),
			),
			foodIdByNormalizedAlias = mapOf("plum tomatoes" to 7),
			measuresByFoodId = emptyMap(),
		)
		val calculator = RecipeNutritionCalculator(lookupIndex)
		val result = calculator.calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 13, rawText = "1 x 400 g Can Plum Tomatoes"),
			),
		)

		result.ingredientResults.single().parsed.quantity shouldBe BigDecimal("400")
		result.ingredientResults.single().parsed.unit shouldBe "g"
		result.ingredientResults.single().parsed.parsedName shouldBe "Plum Tomatoes"
		result.ingredientResults.single().foodMatch?.foodId shouldBe 7
		result.ingredientResults.single().grams shouldBe BigDecimal("400")
		result.ingredientResults.single().gramsSource shouldBe GramWeightSource.MASS
	}
}

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
	}
}

package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionSupplementalMeasuresTest {

	@Test
	fun overlayMeasuresAddsMissingPieceGramsWithoutReplacingStoredUnits() {
		val shallot = NutritionFoodRecord(
			id = 8,
			displayName = "Shallots, raw",
			normalizedName = "shallots raw",
			nutrients = FdcNutrientsPer100g(
				calories = BigDecimal.ZERO,
				protein = null,
				carbohydrates = null,
				fat = null,
				fiber = null,
				sugar = null,
				sodium = null,
			),
		)
		val cabbage = NutritionFoodRecord(
			id = 9,
			displayName = "Cabbage, raw",
			normalizedName = "cabbage raw",
			nutrients = FdcNutrientsPer100g(
				calories = BigDecimal.ZERO,
				protein = null,
				carbohydrates = null,
				fat = null,
				fiber = null,
				sugar = null,
				sodium = null,
			),
		)
		val measures = NutritionSupplementalMeasures.overlayMeasures(
			foods = listOf(shallot, cabbage),
			storedMeasures = mapOf(
				8 to mapOf("tbsp" to BigDecimal("10")),
				9 to mapOf("cup" to BigDecimal("89"), "piece" to BigDecimal("33")),
			),
		)

		measures[8] shouldBe mapOf(
			"tbsp" to BigDecimal("10"),
			"piece" to BigDecimal("30"),
		)
		measures[9]?.get("piece") shouldBe BigDecimal("33")
		measures[9]?.get("cup") shouldBe BigDecimal("89")
	}
}

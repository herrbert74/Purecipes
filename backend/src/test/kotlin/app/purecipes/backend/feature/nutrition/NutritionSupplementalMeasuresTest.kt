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

	@Test
	fun overlayMeasuresAddsPieceGramsForExpandedProduce() {
		val banana = NutritionFoodRecord(
			id = 10,
			displayName = "Bananas, raw",
			normalizedName = "bananas raw",
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
		val radish = NutritionFoodRecord(
			id = 11,
			displayName = "Radishes, raw",
			normalizedName = "radishes raw",
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
		val garlic = NutritionFoodRecord(
			id = 12,
			displayName = "Garlic, raw",
			normalizedName = "garlic raw",
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
		val cheddar = NutritionFoodRecord(
			id = 13,
			displayName = "Cheese, cheddar",
			normalizedName = "cheese cheddar",
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
		val groundClove = NutritionFoodRecord(
			id = 14,
			displayName = "Spices, cloves, ground",
			normalizedName = "spices cloves ground",
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
			foods = listOf(banana, radish, garlic, cheddar, groundClove),
			storedMeasures = emptyMap(),
		)

		measures[10]?.get("piece") shouldBe BigDecimal("118")
		measures[11]?.get("piece") shouldBe BigDecimal("5")
		measures[12]?.get("clove") shouldBe BigDecimal("3")
		measures[12]?.get("piece") shouldBe BigDecimal("3")
		measures[13]?.get("tbsp") shouldBe BigDecimal("7")
		measures[14]?.get("clove") shouldBe BigDecimal("0.1")
	}

	@Test
	fun overlayMeasuresAddsPieceGramsForGelatinSalmonAndChestnuts() {
		val gelatin = NutritionFoodRecord(
			id = 20,
			displayName = "Gelatins, dry powder, unsweetened",
			normalizedName = "gelatins dry powder unsweetened",
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
		val salmon = NutritionFoodRecord(
			id = 21,
			displayName = "Fish, salmon, Atlantic, farmed, raw",
			normalizedName = "fish salmon atlantic farmed raw",
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
		val chestnuts = NutritionFoodRecord(
			id = 22,
			displayName = "Waterchestnuts, chinese, canned, solids and liquids",
			normalizedName = "waterchestnuts chinese canned solids and liquids",
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
			foods = listOf(gelatin, salmon, chestnuts),
			storedMeasures = emptyMap(),
		)

		measures[20]?.get("piece") shouldBe BigDecimal("7")
		measures[21]?.get("piece") shouldBe BigDecimal("170")
		measures[22]?.get("piece") shouldBe BigDecimal("12")
	}

	@Test
	fun overlayMeasuresAddsPieceGramsForChickenMozzarellaArtichokeAndShrimp() {
		val chicken = NutritionFoodRecord(
			id = 30,
			displayName = "Chicken, broilers or fryers, meat and skin, raw",
			normalizedName = "chicken broilers or fryers meat and skin raw",
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
		val mozzarella = NutritionFoodRecord(
			id = 31,
			displayName = "Cheese, mozzarella, whole milk",
			normalizedName = "cheese mozzarella whole milk",
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
		val artichoke = NutritionFoodRecord(
			id = 32,
			displayName = "Artichokes, (globe or french), raw",
			normalizedName = "artichokes globe or french raw",
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
		val shrimp = NutritionFoodRecord(
			id = 33,
			displayName = "Crustaceans, shrimp, mixed species, raw",
			normalizedName = "crustaceans shrimp mixed species raw",
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
			foods = listOf(chicken, mozzarella, artichoke, shrimp),
			storedMeasures = emptyMap(),
		)

		measures[30]?.get("piece") shouldBe BigDecimal("1400")
		measures[31]?.get("piece") shouldBe BigDecimal("125")
		measures[32]?.get("piece") shouldBe BigDecimal("128")
		measures[33]?.get("piece") shouldBe BigDecimal("20")
	}
}

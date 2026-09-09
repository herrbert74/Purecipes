package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class IngredientGramWeightResolverTest {

	@Test
	fun resolveGramsUsesNamedHouseholdMeasureWhenPresent() {
		val resolved = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal.ONE,
			unit = "cup",
			foodMeasures = mapOf("cup" to BigDecimal("120")),
		)

		resolved.shouldNotBeNull()
		resolved.grams shouldBe BigDecimal("120")
		resolved.source shouldBe GramWeightSource.MEASURE
	}

	@Test
	fun resolveGramsFallsBackToWaterDensityWhenTheFoodHasNoNamedMeasure() {
		val resolved = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal.ONE,
			unit = "tsp",
			foodMeasures = emptyMap(),
		)

		resolved.shouldNotBeNull()
		resolved.grams shouldBe BigDecimal("5")
		resolved.source shouldBe GramWeightSource.DENSITY
	}

	@Test
	fun resolveGramsFallsBackToWaterDensityForVolumeUnits() {
		val millilitres = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal("120"),
			unit = "ml",
			foodMeasures = emptyMap(),
		)
		millilitres.shouldNotBeNull()
		millilitres.grams shouldBe BigDecimal("120")
		millilitres.source shouldBe GramWeightSource.DENSITY

		val teaspoon = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal("2"),
			unit = "tsp",
			foodMeasures = emptyMap(),
		)
		teaspoon.shouldNotBeNull()
		teaspoon.grams shouldBe BigDecimal("10")
		teaspoon.source shouldBe GramWeightSource.DENSITY
	}

	@Test
	fun resolveGramsRecordsMassUnitsSeparately() {
		val resolved = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal("50"),
			unit = "g",
			foodMeasures = emptyMap(),
		)

		resolved.shouldNotBeNull()
		resolved.grams shouldBe BigDecimal("50")
		resolved.source shouldBe GramWeightSource.MASS
	}

	@Test
	fun resolveGramsLeavesCountUnitsUnresolvedWithoutAFoodMeasure() {
		IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal.ONE,
			unit = "clove",
			foodMeasures = emptyMap(),
		).shouldBeNull()
	}

	@Test
	fun resolveGramsUsesCloveMeasureWhenPresentOnTheFood() {
		val resolved = IngredientGramWeightResolver.resolveGrams(
			quantity = BigDecimal("2"),
			unit = "clove",
			foodMeasures = mapOf("clove" to BigDecimal("3")),
		)

		resolved.shouldNotBeNull()
		resolved.grams shouldBe BigDecimal("6")
		resolved.source shouldBe GramWeightSource.MEASURE
	}
}

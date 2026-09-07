package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionMeasureNamesTest {

	@Test
	fun resolveImportedNameKeepsFoundationHouseholdUnits() {
		NutritionMeasureNames.resolveImportedName("cup", modifier = null) shouldBe "cup"
		NutritionMeasureNames.resolveImportedName("teaspoon", modifier = null) shouldBe "tsp"
		NutritionMeasureNames.resolveImportedName("each", modifier = null) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName("whole", modifier = null) shouldBe "piece"
	}

	@Test
	fun resolveImportedNameReadsSrLegacyModifierWhenMeasureUnitIsUndetermined() {
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "cup, diced",
		) shouldBe "cup"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "tablespoon",
		) shouldBe "tbsp"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "medium (2-1/2\" dia)",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "fruit (2-3/8\" dia)",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "avocado, NS as to Florida or California",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "Potato medium (2-1/4\" to 3-1/4\" dia)",
		) shouldBe "piece"
	}

	@Test
	fun resolveImportedNameDropsMassUnitsAndUnusablePortions() {
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = "oz").shouldBeNull()
		NutritionMeasureNames.resolveImportedName("g", modifier = null).shouldBeNull()
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = "cake").shouldBeNull()
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = "slice, medium").shouldBeNull()
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = null).shouldBeNull()
	}

	@Test
	fun gramsPerSingleMeasureDividesByPortionAmount() {
		NutritionMeasureNames.gramsPerSingleMeasure(
			gramWeight = BigDecimal("30"),
			amount = BigDecimal("2"),
		) shouldBe BigDecimal("15.0000")
	}
}

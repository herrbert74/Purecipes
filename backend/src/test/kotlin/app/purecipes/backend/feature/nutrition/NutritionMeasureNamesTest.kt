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
	}

	@Test
	fun resolveImportedNameDropsMassUnitsAndUnusablePortions() {
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = "oz").shouldBeNull()
		NutritionMeasureNames.resolveImportedName("g", modifier = null).shouldBeNull()
		NutritionMeasureNames.resolveImportedName("undetermined", modifier = "cake").shouldBeNull()
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

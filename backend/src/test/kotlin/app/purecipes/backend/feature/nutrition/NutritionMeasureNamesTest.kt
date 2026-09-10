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
		NutritionMeasureNames.resolveImportedName("head", modifier = null) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName("ear", modifier = null) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName("stalk", modifier = null) shouldBe "piece"
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
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "head, medium (about 5-3/4\" dia)",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "ear, medium (6-3/4\" to 7-1/2\" long) yields",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "stalk, medium (7-1/2\" - 8\" long)",
		) shouldBe "piece"
		NutritionMeasureNames.resolveImportedName(
			measureUnitName = "undetermined",
			modifier = "pepper, large (3-3/4\" long, 3\" dia)",
		) shouldBe "piece"
	}

	@Test
	fun pieceImportPreferencePrefersMediumHeadsEarsAndStalks() {
		NutritionMeasureNames.pieceImportPreference(
			"head, medium (about 5-3/4\" dia)",
		) shouldBe 100
		NutritionMeasureNames.pieceImportPreference("head, large (about 7\" dia)") shouldBe 80
		NutritionMeasureNames.pieceImportPreference(
			"ear, medium (6-3/4\" to 7-1/2\" long) yields",
		) shouldBe 100
		NutritionMeasureNames.pieceImportPreference("stalk") shouldBe 60
		NutritionMeasureNames.pieceImportPreference("cup, chopped") shouldBe 0
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

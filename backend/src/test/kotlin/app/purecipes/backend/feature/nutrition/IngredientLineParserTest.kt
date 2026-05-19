package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class IngredientLineParserTest {

	@Test
	fun parseReadsQuantityUnitAndName() {
		val parsed = IngredientLineParser.parse("2 cups all-purpose flour")

		parsed.quantity shouldBe BigDecimal("2")
		parsed.unit shouldBe "cup"
		parsed.parsedName shouldBe "all-purpose flour"
		parsed.isMeasurable shouldBe true
	}

	@Test
	fun parseSupportsFractions() {
		val parsed = IngredientLineParser.parse("1/2 tsp salt")

		parsed.quantity shouldBe BigDecimal("0.5000")
		parsed.unit shouldBe "tsp"
		parsed.isMeasurable shouldBe true
	}

	@Test
	fun parseMarksUnstructuredLinesAsNotMeasurable() {
		val parsed = IngredientLineParser.parse("Salt to taste")

		parsed.isMeasurable shouldBe false
	}

	@Test
	fun parseIgnoresSectionHeadings() {
		val parsed = IngredientLineParser.parse("For the sauce:")

		parsed.isMeasurable shouldBe false
	}
}

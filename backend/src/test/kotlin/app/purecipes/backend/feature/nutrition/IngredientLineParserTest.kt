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

	@Test
	fun parseReadsUnicodeAndMixedFractions() {
		val unicode = IngredientLineParser.parse("1½ tsp salt")
		unicode.quantity shouldBe BigDecimal("1.5000")
		unicode.unit shouldBe "tsp"
		unicode.parsedName shouldBe "salt"
		unicode.isMeasurable shouldBe true

		val mixed = IngredientLineParser.parse("3 1/2 ounces sugar")
		mixed.quantity shouldBe BigDecimal("3.5000")
		mixed.unit shouldBe "oz"
		mixed.parsedName shouldBe "sugar"
		mixed.isMeasurable shouldBe true
	}

	@Test
	fun parseSkipsSizeWordsAndCountsEggs() {
		val largeEggs = IngredientLineParser.parse("2 large eggs")
		largeEggs.quantity shouldBe BigDecimal("2")
		largeEggs.unit shouldBe "egg"
		largeEggs.parsedName shouldBe "eggs"
		largeEggs.isMeasurable shouldBe true

		val yolks = IngredientLineParser.parse("6 large egg yolks")
		yolks.unit shouldBe "egg"
		yolks.parsedName shouldBe "egg yolks"
		yolks.isMeasurable shouldBe true

		val plainEggs = IngredientLineParser.parse("2 eggs")
		plainEggs.unit shouldBe "egg"
		plainEggs.parsedName shouldBe "eggs"
		plainEggs.isMeasurable shouldBe true
	}

	@Test
	fun parseUsesParentheticalAmounts() {
		val skipped = IngredientLineParser.parse("1 tablespoon (15 ml) extra-virgin olive oil")
		skipped.quantity shouldBe BigDecimal("1")
		skipped.unit shouldBe "tbsp"
		skipped.parsedName shouldBe "extra-virgin olive oil"
		skipped.isMeasurable shouldBe true

		val leading = IngredientLineParser.parse("(120 ml) water")
		leading.quantity shouldBe BigDecimal("120")
		leading.unit shouldBe "ml"
		leading.parsedName shouldBe "water"
		leading.isMeasurable shouldBe true

		val trailing = IngredientLineParser.parse("3 large eggplants (2½ lb.)")
		trailing.quantity shouldBe BigDecimal("2.5000")
		trailing.unit shouldBe "lb"
		trailing.parsedName shouldBe "eggplants"
		trailing.isMeasurable shouldBe true
	}

	@Test
	fun parseKeepsCloveAsTheUnitForGarlic() {
		val parsed = IngredientLineParser.parse("4 cloves garlic, minced")

		parsed.quantity shouldBe BigDecimal("4")
		parsed.unit shouldBe "clove"
		parsed.parsedName shouldBe "garlic, minced"
		parsed.isMeasurable shouldBe true
	}
}

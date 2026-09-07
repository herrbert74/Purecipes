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

	@Test
	fun parseTreatsBareCloveCountsAsGarlic() {
		val parsed = IngredientLineParser.parse("8 cloves")

		parsed.quantity shouldBe BigDecimal("8")
		parsed.unit shouldBe "clove"
		parsed.parsedName shouldBe "garlic"
		parsed.isMeasurable shouldBe true
	}

	@Test
	fun parseKeepsGroundCloveAsTheSpice() {
		val parsed = IngredientLineParser.parse("1 clove, ground")

		parsed.quantity shouldBe BigDecimal("1")
		parsed.unit shouldBe "clove"
		parsed.parsedName shouldBe "ground clove"
		parsed.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsCountProduceWithoutAnAmountToOnePiece() {
		val onion = IngredientLineParser.parse("Onion")
		onion.quantity shouldBe BigDecimal.ONE
		onion.unit shouldBe "piece"
		onion.parsedName shouldBe "Onion"
		onion.isMeasurable shouldBe true

		val redOnion = IngredientLineParser.parse("Red Onion")
		redOnion.quantity shouldBe BigDecimal.ONE
		redOnion.unit shouldBe "piece"
		redOnion.isMeasurable shouldBe true

		val carrot = IngredientLineParser.parse("Carrot")
		carrot.quantity shouldBe BigDecimal.ONE
		carrot.unit shouldBe "piece"
		carrot.isMeasurable shouldBe true

		val bellPepper = IngredientLineParser.parse("Bell Pepper")
		bellPepper.quantity shouldBe BigDecimal.ONE
		bellPepper.unit shouldBe "piece"
		bellPepper.isMeasurable shouldBe true
	}

	@Test
	fun parseLeavesJuiceAndZestLinesWithoutAmountsUnmeasurable() {
		IngredientLineParser.parse("Juice of 1 lemon").isMeasurable shouldBe false
		IngredientLineParser.parse("Lemon Juice").isMeasurable shouldBe false
		IngredientLineParser.parse("zest of 1 lime").isMeasurable shouldBe false
	}

	@Test
	fun parseLeavesSaltAndOilWithoutAmountsUnmeasurable() {
		IngredientLineParser.parse("Salt").isMeasurable shouldBe false
		IngredientLineParser.parse("Olive Oil").isMeasurable shouldBe false
		IngredientLineParser.parse("Bunch Parsley").isMeasurable shouldBe false
		IngredientLineParser.parse("Black Pepper").isMeasurable shouldBe false
	}

	@Test
	fun parseReadsPackTimesMassAndDropsContainerWords() {
		val oneTin = IngredientLineParser.parse("1 x 400 g Can Plum Tomatoes")
		oneTin.quantity shouldBe BigDecimal("400")
		oneTin.unit shouldBe "g"
		oneTin.parsedName shouldBe "Plum Tomatoes"
		oneTin.isMeasurable shouldBe true

		val twoTins = IngredientLineParser.parse("2 x 400 g Can Plum Tomatoes")
		twoTins.quantity shouldBe BigDecimal("800")
		twoTins.unit shouldBe "g"
		twoTins.parsedName shouldBe "Plum Tomatoes"
		twoTins.isMeasurable shouldBe true

		val halfTin = IngredientLineParser.parse(".5x 400 g Can Chopped Tomatoes")
		halfTin.quantity shouldBe BigDecimal("0.5").multiply(BigDecimal("400"))
		halfTin.unit shouldBe "g"
		halfTin.parsedName shouldBe "Chopped Tomatoes"
		halfTin.isMeasurable shouldBe true

		val coconutMilk = IngredientLineParser.parse("1 x 400 ml Can Coconut Milk")
		coconutMilk.quantity shouldBe BigDecimal("400")
		coconutMilk.unit shouldBe "ml"
		coconutMilk.parsedName shouldBe "Coconut Milk"
		coconutMilk.isMeasurable shouldBe true

		val tinOf = IngredientLineParser.parse("1 x 400 g tin of tomatoes")
		tinOf.parsedName shouldBe "tomatoes"

		val noTimesMarker = IngredientLineParser.parse("1 400 g tin chopped tomatoes")
		noTimesMarker.quantity shouldBe BigDecimal("400")
		noTimesMarker.unit shouldBe "g"
		noTimesMarker.parsedName shouldBe "chopped tomatoes"
		noTimesMarker.isMeasurable shouldBe true
	}

	@Test
	fun parseDropsContainerWordsAfterAPlainMass() {
		val parsed = IngredientLineParser.parse("400 g Can Plum Tomatoes")

		parsed.quantity shouldBe BigDecimal("400")
		parsed.unit shouldBe "g"
		parsed.parsedName shouldBe "Plum Tomatoes"
		parsed.isMeasurable shouldBe true
	}
}

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

		val broccoli = IngredientLineParser.parse("Broccoli")
		broccoli.quantity shouldBe BigDecimal.ONE
		broccoli.unit shouldBe "piece"
		broccoli.isMeasurable shouldBe true

		val cabbage = IngredientLineParser.parse("Red Cabbage")
		cabbage.quantity shouldBe BigDecimal.ONE
		cabbage.unit shouldBe "piece"
		cabbage.isMeasurable shouldBe true

		val cauliflower = IngredientLineParser.parse("Cauliflower")
		cauliflower.quantity shouldBe BigDecimal.ONE
		cauliflower.unit shouldBe "piece"
		cauliflower.isMeasurable shouldBe true

		val celery = IngredientLineParser.parse("Stick Celery")
		celery.quantity shouldBe BigDecimal.ONE
		celery.unit shouldBe "piece"
		celery.isMeasurable shouldBe true

		val corn = IngredientLineParser.parse("Corn On The Cob")
		corn.quantity shouldBe BigDecimal.ONE
		corn.unit shouldBe "piece"
		corn.isMeasurable shouldBe true
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
		IngredientLineParser.parse("Corn").isMeasurable shouldBe false
		IngredientLineParser.parse("Butternut Squash").isMeasurable shouldBe false
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

	@Test
	fun parseReadsWordQuantityAndLabeledOuncePacks() {
		val hyphenated = IngredientLineParser.parse("one 14-ounce can coconut milk")
		hyphenated.quantity shouldBe BigDecimal("14")
		hyphenated.unit shouldBe "oz"
		hyphenated.parsedName shouldBe "coconut milk"
		hyphenated.isMeasurable shouldBe true

		val spaced = IngredientLineParser.parse("one 15 ounce can black beans")
		spaced.quantity shouldBe BigDecimal("15")
		spaced.unit shouldBe "oz"
		spaced.parsedName shouldBe "black beans"
		spaced.isMeasurable shouldBe true

		val twoPacks = IngredientLineParser.parse("two 14-ounce cans chickpeas")
		twoPacks.quantity shouldBe BigDecimal("28")
		twoPacks.unit shouldBe "oz"
		twoPacks.parsedName shouldBe "chickpeas"
		twoPacks.isMeasurable shouldBe true
	}

	@Test
	fun parseReadsTrailingAndParentheticalAboutAmounts() {
		val trailing = IngredientLineParser.parse("Yellow onion, about 8 ounces")
		trailing.quantity shouldBe BigDecimal("8")
		trailing.unit shouldBe "oz"
		trailing.parsedName shouldBe "Yellow onion"
		trailing.isMeasurable shouldBe true

		val parenthetical = IngredientLineParser.parse("Boneless chicken breasts (about 12 ounces)")
		parenthetical.quantity shouldBe BigDecimal("12")
		parenthetical.unit shouldBe "oz"
		parenthetical.parsedName shouldBe "Boneless chicken breasts"
		parenthetical.isMeasurable shouldBe true
	}

	@Test
	fun parseTreatsCountNounsAsPieceWhenQuantityExists() {
		val celeryStalks = IngredientLineParser.parse("3 celery stalks")
		celeryStalks.quantity shouldBe BigDecimal("3")
		celeryStalks.unit shouldBe "piece"
		celeryStalks.parsedName shouldBe "celery"
		celeryStalks.isMeasurable shouldBe true

		val stalksCelery = IngredientLineParser.parse("2 stalks celery")
		stalksCelery.quantity shouldBe BigDecimal("2")
		stalksCelery.unit shouldBe "piece"
		stalksCelery.parsedName shouldBe "celery"
		stalksCelery.isMeasurable shouldBe true

		val stalkCelery = IngredientLineParser.parse("1 stalk celery")
		stalkCelery.quantity shouldBe BigDecimal("1")
		stalkCelery.unit shouldBe "piece"
		stalkCelery.parsedName shouldBe "celery"
		stalkCelery.isMeasurable shouldBe true

		val pieceGinger = IngredientLineParser.parse("1 piece ginger")
		pieceGinger.quantity shouldBe BigDecimal("1")
		pieceGinger.unit shouldBe "piece"
		pieceGinger.parsedName shouldBe "ginger"
		pieceGinger.isMeasurable shouldBe true

		val piecesOfGinger = IngredientLineParser.parse("2 pieces of ginger")
		piecesOfGinger.quantity shouldBe BigDecimal("2")
		piecesOfGinger.unit shouldBe "piece"
		piecesOfGinger.parsedName shouldBe "ginger"
		piecesOfGinger.isMeasurable shouldBe true

		val sprigsThyme = IngredientLineParser.parse("3 sprigs thyme")
		sprigsThyme.quantity shouldBe BigDecimal("3")
		sprigsThyme.unit shouldBe "piece"
		sprigsThyme.parsedName shouldBe "thyme"
		sprigsThyme.isMeasurable shouldBe true

		val sprigRosemary = IngredientLineParser.parse("1 sprig rosemary")
		sprigRosemary.quantity shouldBe BigDecimal("1")
		sprigRosemary.unit shouldBe "piece"
		sprigRosemary.parsedName shouldBe "rosemary"
		sprigRosemary.isMeasurable shouldBe true

		val knobGinger = IngredientLineParser.parse("1 knob ginger")
		knobGinger.quantity shouldBe BigDecimal("1")
		knobGinger.unit shouldBe "piece"
		knobGinger.parsedName shouldBe "ginger"
		knobGinger.isMeasurable shouldBe true

		val bulbGarlic = IngredientLineParser.parse("1 bulb garlic")
		bulbGarlic.quantity shouldBe BigDecimal("1")
		bulbGarlic.unit shouldBe "piece"
		bulbGarlic.parsedName shouldBe "garlic"
		bulbGarlic.isMeasurable shouldBe true

		val bunchParsley = IngredientLineParser.parse("1 bunch parsley")
		bunchParsley.quantity shouldBe BigDecimal("1")
		bunchParsley.unit shouldBe "piece"
		bunchParsley.parsedName shouldBe "parsley"
		bunchParsley.isMeasurable shouldBe true
	}

	@Test
	fun parseTreatsBayLeavesWithQuantityAsPiece() {
		val plural = IngredientLineParser.parse("2 bay leaves")
		plural.quantity shouldBe BigDecimal("2")
		plural.unit shouldBe "piece"
		plural.parsedName shouldBe "bay leaf"
		plural.isMeasurable shouldBe true

		val singular = IngredientLineParser.parse("1 bay leaf")
		singular.quantity shouldBe BigDecimal("1")
		singular.unit shouldBe "piece"
		singular.parsedName shouldBe "bay leaf"
		singular.isMeasurable shouldBe true

		val wordQuantity = IngredientLineParser.parse("a bay leaf")
		wordQuantity.quantity shouldBe BigDecimal.ONE
		wordQuantity.unit shouldBe "piece"
		wordQuantity.parsedName shouldBe "bay leaf"
		wordQuantity.isMeasurable shouldBe true
	}

	@Test
	fun parseCountsChickenBreastsThighsRashersAndSausagesAsPiece() {
		val breasts = IngredientLineParser.parse("2 chicken breasts")
		breasts.quantity shouldBe BigDecimal("2")
		breasts.unit shouldBe "piece"
		breasts.parsedName shouldBe "chicken breasts"
		breasts.isMeasurable shouldBe true

		val boneless = IngredientLineParser.parse("2 boneless skinless chicken breasts")
		boneless.quantity shouldBe BigDecimal("2")
		boneless.unit shouldBe "piece"
		boneless.parsedName shouldBe "boneless skinless chicken breasts"
		boneless.isMeasurable shouldBe true

		val thighs = IngredientLineParser.parse("4 chicken thighs")
		thighs.quantity shouldBe BigDecimal("4")
		thighs.unit shouldBe "piece"
		thighs.parsedName shouldBe "chicken thighs"
		thighs.isMeasurable shouldBe true

		val rashers = IngredientLineParser.parse("4 rashers bacon")
		rashers.quantity shouldBe BigDecimal("4")
		rashers.unit shouldBe "piece"
		rashers.parsedName shouldBe "bacon"
		rashers.isMeasurable shouldBe true

		val baconRashers = IngredientLineParser.parse("4 bacon rashers")
		baconRashers.quantity shouldBe BigDecimal("4")
		baconRashers.unit shouldBe "piece"
		baconRashers.parsedName shouldBe "bacon"
		baconRashers.isMeasurable shouldBe true

		val sausages = IngredientLineParser.parse("2 sausages")
		sausages.quantity shouldBe BigDecimal("2")
		sausages.unit shouldBe "piece"
		sausages.parsedName shouldBe "sausages"
		sausages.isMeasurable shouldBe true

		val porkSausage = IngredientLineParser.parse("1 pork sausage")
		porkSausage.quantity shouldBe BigDecimal("1")
		porkSausage.unit shouldBe "piece"
		porkSausage.parsedName shouldBe "pork sausage"
		porkSausage.isMeasurable shouldBe true
	}

	@Test
	fun parseLeavesBareMeatAndHerbLinesWithoutAmountsUnmeasurable() {
		IngredientLineParser.parse("chicken breast").isMeasurable shouldBe false
		IngredientLineParser.parse("sausage").isMeasurable shouldBe false
		IngredientLineParser.parse("bay leaf").isMeasurable shouldBe false
		IngredientLineParser.parse("Salt to taste").isMeasurable shouldBe false
		IngredientLineParser.parse("Olive oil").isMeasurable shouldBe false
	}
}

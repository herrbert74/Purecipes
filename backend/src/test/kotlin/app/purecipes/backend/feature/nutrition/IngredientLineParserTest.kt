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
		val parsed = IngredientLineParser.parse("12 large")

		parsed.isMeasurable shouldBe false
		NutritionNameNormalizer.hasMeaningfulFoodName(parsed.parsedName) shouldBe false
	}

	@Test
	fun parseDefaultsSaltAndOilWithoutAmounts() {
		val salt = IngredientLineParser.parse("Salt to taste")
		salt.quantity shouldBe BigDecimal.ONE
		salt.unit shouldBe "tsp"
		salt.isMeasurable shouldBe true

		val oil = IngredientLineParser.parse("Olive Oil")
		oil.quantity shouldBe BigDecimal.ONE
		oil.unit shouldBe "tbsp"
		oil.isMeasurable shouldBe true
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

		val pickledTurnips = IngredientLineParser.parse("Pickled turnips")
		pickledTurnips.quantity shouldBe BigDecimal.ONE
		pickledTurnips.unit shouldBe "piece"
		pickledTurnips.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsJuiceAndZestLinesWithoutAmounts() {
		val lemonJuice = IngredientLineParser.parse("Lemon Juice")
		lemonJuice.quantity shouldBe BigDecimal.ONE
		lemonJuice.unit shouldBe "tbsp"
		lemonJuice.isMeasurable shouldBe true

		val juiceOfLemon = IngredientLineParser.parse("Juice of 1 lemon")
		juiceOfLemon.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsCommonPantryLinesWithoutAmounts() {
		IngredientLineParser.parse("Salt").isMeasurable shouldBe true
		IngredientLineParser.parse("Olive Oil").isMeasurable shouldBe true
		IngredientLineParser.parse("Black Pepper").isMeasurable shouldBe true
		IngredientLineParser.parse("Butternut Squash").isMeasurable shouldBe true
		IngredientLineParser.parse("Corn").isMeasurable shouldBe false
		IngredientLineParser.parse("chicken breast").isMeasurable shouldBe false
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
		sprigsThyme.unit shouldBe "tsp"
		sprigsThyme.parsedName shouldBe "thyme"
		sprigsThyme.isMeasurable shouldBe true

		val sprigRosemary = IngredientLineParser.parse("1 sprig rosemary")
		sprigRosemary.quantity shouldBe BigDecimal("1")
		sprigRosemary.unit shouldBe "tsp"
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
		bunchParsley.unit shouldBe "tbsp"
		bunchParsley.parsedName shouldBe "parsley"
		bunchParsley.isMeasurable shouldBe true
	}

	@Test
	fun parseTreatsBayLeavesWithQuantityAsTeaspoons() {
		val plural = IngredientLineParser.parse("2 bay leaves")
		plural.quantity shouldBe BigDecimal("2")
		plural.unit shouldBe "tsp"
		plural.parsedName shouldBe "bay leaf"
		plural.isMeasurable shouldBe true

		val singular = IngredientLineParser.parse("1 bay leaf")
		singular.quantity shouldBe BigDecimal("1")
		singular.unit shouldBe "tsp"
		singular.parsedName shouldBe "bay leaf"
		singular.isMeasurable shouldBe true

		val wordQuantity = IngredientLineParser.parse("a bay leaf")
		wordQuantity.quantity shouldBe BigDecimal.ONE
		wordQuantity.unit shouldBe "tsp"
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
	fun parseLeavesBareMeatLinesWithoutAmountsUnmeasurable() {
		IngredientLineParser.parse("chicken breast").isMeasurable shouldBe false
		IngredientLineParser.parse("Corn").isMeasurable shouldBe false
	}

	@Test
	fun parseDefaultsBareSeasoningLinesWithoutAmounts() {
		IngredientLineParser.parse("bay leaf").isMeasurable shouldBe true
		IngredientLineParser.parse("Salt to taste").isMeasurable shouldBe true
		IngredientLineParser.parse("Olive oil").isMeasurable shouldBe true
		IngredientLineParser.parse("sausage").isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsBareGarlicToOneClove() {
		val garlic = IngredientLineParser.parse("garlic")
		garlic.quantity shouldBe BigDecimal.ONE
		garlic.unit shouldBe "clove"
		garlic.parsedName shouldBe "garlic"
		garlic.isMeasurable shouldBe true

		val minced = IngredientLineParser.parse("garlic, minced")
		minced.quantity shouldBe BigDecimal.ONE
		minced.unit shouldBe "clove"
		minced.isMeasurable shouldBe true

		val counted = IngredientLineParser.parse("2 garlic")
		counted.quantity shouldBe BigDecimal("2")
		counted.unit shouldBe "clove"
		counted.isMeasurable shouldBe true

		val powder = IngredientLineParser.parse("garlic powder")
		powder.quantity shouldBe BigDecimal.ONE
		powder.unit shouldBe "tsp"
		powder.isMeasurable shouldBe true

		IngredientLineParser.parse("garlic bread").isMeasurable shouldBe false
	}

	@Test
	fun parseDefaultsMoreWholeProduceAndPantryLines() {
		val radish = IngredientLineParser.parse("radish")
		radish.quantity shouldBe BigDecimal.ONE
		radish.unit shouldBe "piece"
		radish.isMeasurable shouldBe true

		val mushrooms = IngredientLineParser.parse("mushrooms")
		mushrooms.unit shouldBe "piece"
		mushrooms.isMeasurable shouldBe true

		val redPeppers = IngredientLineParser.parse("2 red peppers")
		redPeppers.quantity shouldBe BigDecimal("2")
		redPeppers.unit shouldBe "piece"
		redPeppers.isMeasurable shouldBe true

		val honey = IngredientLineParser.parse("honey")
		honey.quantity shouldBe BigDecimal.ONE
		honey.unit shouldBe "tbsp"
		honey.isMeasurable shouldBe true

		val parmesan = IngredientLineParser.parse("parmesan")
		parmesan.unit shouldBe "tbsp"
		parmesan.isMeasurable shouldBe true

		val stock = IngredientLineParser.parse("chicken stock")
		stock.unit shouldBe "cup"
		stock.isMeasurable shouldBe true
	}

	@Test
	fun parseTreatsLeadingHeadsHandfulsAndSlicesWithoutQuantity() {
		val headGarlic = IngredientLineParser.parse("head garlic")
		headGarlic.quantity shouldBe BigDecimal.ONE
		headGarlic.unit shouldBe "piece"
		headGarlic.parsedName shouldBe "garlic"
		headGarlic.isMeasurable shouldBe true

		val handfuls = IngredientLineParser.parse("generous handfuls parmigiano reggiano")
		handfuls.quantity shouldBe BigDecimal("2")
		handfuls.unit shouldBe "tbsp"
		handfuls.parsedName shouldBe "parmigiano reggiano"
		handfuls.isMeasurable shouldBe true

		val slices = IngredientLineParser.parse("thin slices prosciutto")
		slices.quantity shouldBe BigDecimal.ONE
		slices.unit shouldBe "piece"
		slices.parsedName shouldBe "prosciutto"
		slices.isMeasurable shouldBe true
	}

	@Test
	fun parseStripsExpandedContainersAndOrphanUnits() {
		val canned = IngredientLineParser.parse("1 can pumpkin puree")
		canned.quantity shouldBe BigDecimal.ONE
		canned.unit shouldBe "piece"
		canned.parsedName shouldBe "pumpkin puree"
		canned.isMeasurable shouldBe true

		val bagged = IngredientLineParser.parse("1 bag baby spinach")
		bagged.parsedName shouldBe "baby spinach"

		val trailingUnit = IngredientLineParser.parse("olive oil tbsp")
		trailingUnit.unit shouldBe "tbsp"
		trailingUnit.parsedName shouldBe "olive oil"
		trailingUnit.isMeasurable shouldBe true

		val leadingUnit = IngredientLineParser.parse("tbsp olive oil")
		leadingUnit.unit shouldBe "tbsp"
		leadingUnit.parsedName shouldBe "olive oil"
		leadingUnit.isMeasurable shouldBe true
	}

	@Test
	fun parseReadsFewDashesAsPinchTeaspoons() {
		val dashes = IngredientLineParser.parse("few dashes hot sauce")
		dashes.quantity shouldBe BigDecimal("0.75")
		dashes.unit shouldBe "tsp"
		dashes.parsedName shouldBe "hot sauce"
		dashes.isMeasurable shouldBe true
	}

	@Test
	fun parseTreatsTrailingFiletsAndSheetsWithoutQuantityAsPiece() {
		val salmon = IngredientLineParser.parse("boneless skinless salmon filets")
		salmon.quantity shouldBe BigDecimal.ONE
		salmon.unit shouldBe "piece"
		salmon.parsedName shouldBe "boneless skinless salmon"
		salmon.isMeasurable shouldBe true

		val counted = IngredientLineParser.parse("2 boneless skinless salmon filets")
		counted.quantity shouldBe BigDecimal("2")
		counted.unit shouldBe "piece"
		counted.parsedName shouldBe "boneless skinless salmon"
		counted.isMeasurable shouldBe true

		val countedWithEach = IngredientLineParser.parse("4 boneless skinless salmon filets (about 6 ounces each)")
		countedWithEach.quantity shouldBe BigDecimal("4")
		countedWithEach.unit shouldBe "piece"
		countedWithEach.parsedName shouldBe "boneless skinless salmon"
		countedWithEach.isMeasurable shouldBe true

		val sheets = IngredientLineParser.parse("graham cracker sheets")
		sheets.quantity shouldBe BigDecimal.ONE
		sheets.unit shouldBe "piece"
		sheets.parsedName shouldBe "graham cracker"
		sheets.isMeasurable shouldBe true

		val countedSheets = IngredientLineParser.parse("9 graham cracker sheets (about 5 oz.; 1 sleeve)")
		countedSheets.quantity shouldBe BigDecimal("9")
		countedSheets.unit shouldBe "piece"
		countedSheets.parsedName shouldBe "graham cracker"
		countedSheets.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsCheeseGelatinChestnutsGreensAndBurgers() {
		val cheddar = IngredientLineParser.parse("cheddar cheese")
		cheddar.quantity shouldBe BigDecimal.ONE
		cheddar.unit shouldBe "tbsp"
		cheddar.isMeasurable shouldBe true

		val gelatin = IngredientLineParser.parse("unflavored gelatin")
		gelatin.quantity shouldBe BigDecimal.ONE
		gelatin.unit shouldBe "piece"
		gelatin.isMeasurable shouldBe true

		val chestnuts = IngredientLineParser.parse("water chestnuts")
		chestnuts.unit shouldBe "piece"
		chestnuts.isMeasurable shouldBe true

		val radicchio = IngredientLineParser.parse("radicchio")
		radicchio.unit shouldBe "piece"
		radicchio.isMeasurable shouldBe true

		val greens = IngredientLineParser.parse("baby salad greens")
		greens.unit shouldBe "piece"
		greens.isMeasurable shouldBe true

		val burger = IngredientLineParser.parse("plant based burger")
		burger.unit shouldBe "piece"
		burger.isMeasurable shouldBe true

		val cloveOnly = IngredientLineParser.parse("1 clove")
		cloveOnly.quantity shouldBe BigDecimal.ONE
		cloveOnly.unit shouldBe "clove"
		cloveOnly.parsedName shouldBe "garlic"
		cloveOnly.isMeasurable shouldBe true

		val graterCloves = IngredientLineParser.parse("4 cloves the small side of a box grater")
		graterCloves.quantity shouldBe BigDecimal("4")
		graterCloves.unit shouldBe "clove"
		graterCloves.parsedName shouldBe "garlic"
		graterCloves.isMeasurable shouldBe true
	}

	@Test
	fun parseKeepsWholeClovesAsSpiceInsteadOfOrphaningWhole() {
		val counted = IngredientLineParser.parse("4 whole cloves")
		counted.quantity shouldBe BigDecimal("4")
		counted.unit shouldBe "tsp"
		counted.parsedName shouldBe "whole cloves"
		counted.isMeasurable shouldBe true

		val bare = IngredientLineParser.parse("whole cloves")
		bare.quantity shouldBe BigDecimal.ONE
		bare.unit shouldBe "tsp"
		bare.parsedName shouldBe "whole cloves"
		bare.isMeasurable shouldBe true
	}

	@Test
	fun parseCountsWholeChickenLegsAndContainersAsPiece() {
		val chicken = IngredientLineParser.parse("chicken")
		chicken.quantity shouldBe BigDecimal.ONE
		chicken.unit shouldBe "piece"
		chicken.isMeasurable shouldBe true

		val wholeChicken = IngredientLineParser.parse("whole chicken")
		wholeChicken.unit shouldBe "piece"
		wholeChicken.isMeasurable shouldBe true

		val roasting = IngredientLineParser.parse("roasting chickens")
		roasting.unit shouldBe "piece"
		roasting.isMeasurable shouldBe true

		val leg = IngredientLineParser.parse("chicken leg")
		leg.unit shouldBe "piece"
		leg.isMeasurable shouldBe true

		val countedLegs = IngredientLineParser.parse("2 chicken legs")
		countedLegs.quantity shouldBe BigDecimal("2")
		countedLegs.unit shouldBe "piece"
		countedLegs.isMeasurable shouldBe true

		val mozzarella = IngredientLineParser.parse("ball mozzarella")
		mozzarella.quantity shouldBe BigDecimal.ONE
		mozzarella.unit shouldBe "piece"
		mozzarella.parsedName shouldBe "mozzarella"
		mozzarella.isMeasurable shouldBe true

		val halloumi = IngredientLineParser.parse("1 block halloumi")
		halloumi.quantity shouldBe BigDecimal("1")
		halloumi.unit shouldBe "piece"
		halloumi.parsedName shouldBe "halloumi"
		halloumi.isMeasurable shouldBe true

		val chocolate = IngredientLineParser.parse("bar bittersweet chocolate")
		chocolate.unit shouldBe "piece"
		chocolate.parsedName shouldBe "bittersweet chocolate"
		chocolate.isMeasurable shouldBe true

		val loaf = IngredientLineParser.parse("loaf challah")
		loaf.unit shouldBe "piece"
		loaf.parsedName shouldBe "challah"
		loaf.isMeasurable shouldBe true

		val artichoke = IngredientLineParser.parse("artichoke")
		artichoke.unit shouldBe "piece"
		artichoke.isMeasurable shouldBe true

		val prawns = IngredientLineParser.parse("king prawns")
		prawns.unit shouldBe "piece"
		prawns.isMeasurable shouldBe true
	}

	@Test
	fun parseMapsSplashShotPintAndDropsToHouseholdUnits() {
		val splash = IngredientLineParser.parse("splash of vanilla extract")
		splash.quantity shouldBe BigDecimal.ONE
		splash.unit shouldBe "tsp"
		splash.parsedName shouldBe "vanilla extract"
		splash.isMeasurable shouldBe true

		val shot = IngredientLineParser.parse("shot brandy")
		shot.quantity shouldBe BigDecimal.ONE
		shot.unit shouldBe "tbsp"
		shot.parsedName shouldBe "brandy"
		shot.isMeasurable shouldBe true

		val glass = IngredientLineParser.parse("glass dry white wine")
		glass.unit shouldBe "cup"
		glass.parsedName shouldBe "dry white wine"
		glass.isMeasurable shouldBe true

		val pint = IngredientLineParser.parse("pint blackberries")
		pint.quantity shouldBe BigDecimal("2")
		pint.unit shouldBe "cup"
		pint.parsedName shouldBe "blackberries"
		pint.isMeasurable shouldBe true

		val drops = IngredientLineParser.parse("drops fish sauce")
		drops.unit shouldBe "tsp"
		drops.parsedName shouldBe "fish sauce"
		drops.isMeasurable shouldBe true

		val vodka = IngredientLineParser.parse("vodka")
		vodka.quantity shouldBe BigDecimal.ONE
		vodka.unit shouldBe "tbsp"
		vodka.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsSaucesYeastWaterAndBayleaves() {
		val hotSauce = IngredientLineParser.parse("hot sauce")
		hotSauce.unit shouldBe "tbsp"
		hotSauce.isMeasurable shouldBe true

		val garlicPaste = IngredientLineParser.parse("garlic paste")
		garlicPaste.unit shouldBe "tbsp"
		garlicPaste.isMeasurable shouldBe true

		val zhug = IngredientLineParser.parse("zhug")
		zhug.quantity shouldBe BigDecimal.ONE
		zhug.unit shouldBe "tbsp"
		zhug.parsedName.lowercase() shouldBe "zhug"
		zhug.isMeasurable shouldBe true

		val yeast = IngredientLineParser.parse("active dry yeast")
		yeast.unit shouldBe "tsp"
		yeast.isMeasurable shouldBe true

		val water = IngredientLineParser.parse("water")
		water.unit shouldBe "cup"
		water.isMeasurable shouldBe true

		val bayleaves = IngredientLineParser.parse("bayleaves")
		bayleaves.quantity shouldBe BigDecimal.ONE
		bayleaves.unit shouldBe "tsp"
		bayleaves.parsedName shouldBe "bay leaf"
		bayleaves.isMeasurable shouldBe true

		val fiveSpice = IngredientLineParser.parse("chinese five spice")
		fiveSpice.unit shouldBe "tsp"
		fiveSpice.isMeasurable shouldBe true
	}

	@Test
	fun parseLeavesOrphanModifiersAndAmbiguousLinesUnmeasurable() {
		IngredientLineParser.parse("whole").isMeasurable shouldBe false
		IngredientLineParser.parse("white").isMeasurable shouldBe false
		IngredientLineParser.parse("vegetable").isMeasurable shouldBe false
		IngredientLineParser.parse("one").isMeasurable shouldBe false
		IngredientLineParser.parse("warm").isMeasurable shouldBe false
		IngredientLineParser.parse("store-bought").isMeasurable shouldBe false
		IngredientLineParser.parse("yellow").isMeasurable shouldBe false
		IngredientLineParser.parse("kosher").isMeasurable shouldBe false
		IngredientLineParser.parse("soft").isMeasurable shouldBe false
		IngredientLineParser.parse("toppings").isMeasurable shouldBe false
		IngredientLineParser.parse("hot vegetable").isMeasurable shouldBe false
		IngredientLineParser.parse("aromatic herbs vegetables your choosing").isMeasurable shouldBe false
		IngredientLineParser.parse("chicken breast").isMeasurable shouldBe false
		IngredientLineParser.parse("1 medium white").isMeasurable shouldBe false
		IngredientLineParser.parse("1 yellow").isMeasurable shouldBe false
		IngredientLineParser.parse("2 whole").isMeasurable shouldBe false
		IngredientLineParser.parse("6 hard boiled").isMeasurable shouldBe false
		IngredientLineParser.parse("1/2 a large white (see above)").isMeasurable shouldBe false
		IngredientLineParser.parse("Vegetable fat, for frying").isMeasurable shouldBe false
		IngredientLineParser.parse("One 14-ounce can (see notes)").isMeasurable shouldBe false
		IngredientLineParser.parse("5 tbsp vegetable").isMeasurable shouldBe false
		IngredientLineParser.parse("Vegetable oil").isMeasurable shouldBe true
		IngredientLineParser.parse("white rice").isMeasurable shouldBe true
		IngredientLineParser.parse("one egg").isMeasurable shouldBe true
	}

	@Test
	fun parseRecoversPacksPiecesSeedsPeelAndNutsFromMeasurableLines() {
		val crepes = IngredientLineParser.parse("crepes")
		crepes.quantity shouldBe BigDecimal.ONE
		crepes.unit shouldBe "piece"
		crepes.isMeasurable shouldBe true

		val vanilla = IngredientLineParser.parse("Seeds from 1 vanilla pod")
		vanilla.quantity shouldBe BigDecimal.ONE
		vanilla.unit shouldBe "tsp"
		vanilla.parsedName shouldBe "vanilla"
		vanilla.isMeasurable shouldBe true

		val pouches = IngredientLineParser.parse("2 x Blue Dragon Satay Season & Stir Fry pouches")
		pouches.quantity shouldBe BigDecimal("2")
		pouches.unit shouldBe "piece"
		pouches.isMeasurable shouldBe true

		val cassava = IngredientLineParser.parse(
			"1 1-lb. small cassava, peeled, woody center removed, cubed",
		)
		cassava.quantity shouldBe BigDecimal.ONE
		cassava.unit shouldBe "lb"
		cassava.isMeasurable shouldBe true

		val morcilla = IngredientLineParser.parse("1 morcilla de burgos (Spanish black pudding)")
		morcilla.quantity shouldBe BigDecimal.ONE
		morcilla.unit shouldBe "piece"
		morcilla.isMeasurable shouldBe true

		val masala = IngredientLineParser.parse("3 heaped tsp East End Masala Mix of your choice")
		masala.quantity shouldBe BigDecimal("3")
		masala.unit shouldBe "tsp"
		masala.isMeasurable shouldBe true

		val karaage = IngredientLineParser.parse("1 recipe Japanese-style karaage")
		karaage.quantity shouldBe BigDecimal.ONE
		karaage.unit shouldBe "piece"
		karaage.isMeasurable shouldBe true

		val driedLimes = IngredientLineParser.parse(
			"6 larger (total weight about 1 ounce; 28 g)",
		)
		driedLimes.isMeasurable shouldBe false

		val peel = IngredientLineParser.parse("Peel (without any pith) from a ripe lemon")
		peel.quantity shouldBe BigDecimal.ONE
		peel.unit shouldBe "tbsp"
		peel.isMeasurable shouldBe true

		val nuts = IngredientLineParser.parse("chopped walnuts, pecans,")
		nuts.unit shouldBe "tbsp"
		nuts.isMeasurable shouldBe true

		val tapatio = IngredientLineParser.parse("Tapatío")
		tapatio.unit shouldBe "tbsp"
		tapatio.isMeasurable shouldBe true

		val oniony = IngredientLineParser.parse("'Something oniony' such as 1 spring onion")
		oniony.quantity shouldBe BigDecimal.ONE
		oniony.unit shouldBe "piece"
		oniony.isMeasurable shouldBe true
	}

	@Test
	fun parseRecoversPuffPastryCoupleRosemaryChickenAndPlusClauses() {
		val pastry = IngredientLineParser.parse("One 9-inch square sheet frozen puff pastry, thawed")
		pastry.quantity shouldBe BigDecimal.ONE
		pastry.unit shouldBe "piece"
		pastry.parsedName.lowercase().contains("puff pastry") shouldBe true
		pastry.isMeasurable shouldBe true

		val rosemary = IngredientLineParser.parse("A couple of stalks of rosemary")
		rosemary.quantity shouldBe BigDecimal("2")
		rosemary.unit shouldBe "piece"
		rosemary.parsedName.lowercase().contains("rosemary") shouldBe true
		rosemary.isMeasurable shouldBe true

		val chicken = IngredientLineParser.parse("1 (1.8 kg chicken) cut into 12 pieces")
		chicken.quantity shouldBe BigDecimal("1.8")
		chicken.unit shouldBe "kg"
		chicken.parsedName.lowercase().contains("chicken") shouldBe true
		chicken.isMeasurable shouldBe true

		val sugar = IngredientLineParser.parse(
			"3 Tbsp. plus ½ cup (packed; 138 g) dark brown sugar, divided",
		)
		sugar.unit shouldBe "g"
		sugar.quantity shouldBe BigDecimal("138")
		sugar.parsedName.lowercase().contains("sugar") shouldBe true
		sugar.isMeasurable shouldBe true

		val bottle = IngredientLineParser.parse("1 (350 ml) bottle")
		bottle.isMeasurable shouldBe false

		val teaspoon = IngredientLineParser.parse("1/2 teaspoon")
		teaspoon.isMeasurable shouldBe false

		val head = IngredientLineParser.parse("1 large head")
		head.isMeasurable shouldBe false
	}

	@Test
	fun parseRecoversPackFoodsPoppySeedsAndCenterCutRanges() {
		val broth = IngredientLineParser.parse("1 (10.5 ounce can) low-sodium beef broth")
		broth.quantity shouldBe BigDecimal("10.5")
		broth.unit shouldBe "oz"
		broth.parsedName.lowercase().contains("beef broth") shouldBe true
		broth.isMeasurable shouldBe true

		val condensed = IngredientLineParser.parse(
			"1 (14-oz. can) sweetened condensed milk, unopened, label removed",
		)
		condensed.quantity shouldBe BigDecimal("14")
		condensed.unit shouldBe "oz"
		condensed.parsedName.lowercase().contains("condensed milk") shouldBe true
		condensed.parsedName.lowercase().contains("unopened") shouldBe false
		condensed.isMeasurable shouldBe true

		val pastry = IngredientLineParser.parse(
			"1 sheet puff pastry from 1 (14-ounce package) frozen puff pastry, thawed " +
				"(such as Dufour or Pepperidge Farm)",
		)
		pastry.quantity shouldBe BigDecimal.ONE
		pastry.unit shouldBe "piece"
		pastry.parsedName.lowercase() shouldBe "puff pastry"
		pastry.isMeasurable shouldBe true

		val poppy = IngredientLineParser.parse("poppy seeds, optional")
		poppy.quantity shouldBe BigDecimal.ONE
		poppy.unit shouldBe "tbsp"
		poppy.parsedName.lowercase().contains("poppy") shouldBe true
		poppy.isMeasurable shouldBe true

		val filets = IngredientLineParser.parse(
			"Two 6- to 8-ounce center-cut filets mignon, 1 1/2 to 2 inches thick, trimmed (see notes)",
		)
		filets.quantity shouldBe BigDecimal("12")
		filets.unit shouldBe "oz"
		filets.parsedName.lowercase().contains("mignon") shouldBe true
		filets.parsedName.lowercase().contains("- to 8-ounce") shouldBe false
		filets.isMeasurable shouldBe true

		val potatoes = IngredientLineParser.parse(
			"2 large yellow potatoes (1 pound total), cut into 1 1/2-inch pieces",
		)
		potatoes.quantity shouldBe BigDecimal.ONE
		potatoes.unit shouldBe "lb"
		potatoes.parsedName.lowercase().contains("potato") shouldBe true
		potatoes.parsedName.lowercase().contains("total") shouldBe false
		potatoes.isMeasurable shouldBe true

		val cube = IngredientLineParser.parse(
			"1 3/8 kg cube steak (tenderized round steak that's been extra tenderized)",
		)
		cube.unit shouldBe "kg"
		cube.parsedName.lowercase().contains("cube steak") shouldBe true
		cube.parsedName.lowercase().contains("been") shouldBe false
		cube.isMeasurable shouldBe true

		val white = IngredientLineParser.parse("1 Tbsp. white")
		white.isMeasurable shouldBe false
	}

	@Test
	fun parseUsesParentheticalPackWeightsAndQuantityRanges() {
		val corn = IngredientLineParser.parse("1 (15.25 ounce) can whole kernel corn, drained")
		corn.quantity shouldBe BigDecimal("15.25")
		corn.unit shouldBe "oz"
		corn.parsedName shouldBe "whole kernel corn, drained"
		corn.isMeasurable shouldBe true

		val pork = IngredientLineParser.parse("1 (5 pound) pork butt roast")
		pork.quantity shouldBe BigDecimal("5")
		pork.unit shouldBe "lb"
		pork.parsedName shouldBe "pork butt roast"
		pork.isMeasurable shouldBe true

		val spaghetti = IngredientLineParser.parse("1 (410 g) box multigrain spaghetti")
		spaghetti.quantity shouldBe BigDecimal("410")
		spaghetti.unit shouldBe "g"
		spaghetti.parsedName shouldBe "multigrain spaghetti"
		spaghetti.isMeasurable shouldBe true

		val verjus = IngredientLineParser.parse("2 to 3 tbsp. verjus")
		verjus.quantity shouldBe BigDecimal("2")
		verjus.unit shouldBe "tbsp"
		verjus.parsedName shouldBe "verjus"
		verjus.isMeasurable shouldBe true

		val drippings = IngredientLineParser.parse("1–2 cups strained and skimmed roast turkey drippings")
		drippings.quantity shouldBe BigDecimal("1")
		drippings.unit shouldBe "cup"
		drippings.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsPreparedFoodsPacksAndCountedShells() {
		val tacos = IngredientLineParser.parse("6 taco shells")
		tacos.quantity shouldBe BigDecimal("6")
		tacos.unit shouldBe "piece"
		tacos.parsedName shouldBe "taco"
		tacos.isMeasurable shouldBe true

		val pastry = IngredientLineParser.parse("1 Pack Puff Pastry")
		pastry.quantity shouldBe BigDecimal("1")
		pastry.unit shouldBe "piece"
		pastry.parsedName shouldBe "Puff Pastry"
		pastry.isMeasurable shouldBe true

		val scoops = IngredientLineParser.parse("2 scoops protein powder")
		scoops.quantity shouldBe BigDecimal("2")
		scoops.unit shouldBe "tbsp"
		scoops.parsedName shouldBe "protein powder"
		scoops.isMeasurable shouldBe true

		val capsicum = IngredientLineParser.parse("1/2 red capsicum, diced")
		capsicum.quantity shouldBe BigDecimal("0.5000")
		capsicum.unit shouldBe "piece"
		capsicum.isMeasurable shouldBe true

		val chips = IngredientLineParser.parse("Corn chips")
		chips.quantity shouldBe BigDecimal.ONE
		chips.unit shouldBe "piece"
		chips.isMeasurable shouldBe true

		val yukon = IngredientLineParser.parse("2 small Yukon gold")
		yukon.quantity shouldBe BigDecimal("2")
		yukon.unit shouldBe "piece"
		yukon.isMeasurable shouldBe true

		val roast = IngredientLineParser.parse("1 small beef chuck roast, about 2 pounds total")
		roast.quantity shouldBe BigDecimal("2")
		roast.unit shouldBe "lb"
		roast.parsedName shouldBe "beef chuck roast"
		roast.isMeasurable shouldBe true

		val cob = IngredientLineParser.parse("6 fresh sweetcorn cobs, husked")
		cob.quantity shouldBe BigDecimal("6")
		cob.unit shouldBe "piece"
		cob.isMeasurable shouldBe true

		val british = IngredientLineParser.parse("1 recipe British chips")
		british.quantity shouldBe BigDecimal("1")
		british.unit shouldBe "piece"
		british.parsedName shouldBe "British chips"
		british.isMeasurable shouldBe true
	}

	@Test
	fun parseReadsExtraWordQuantitiesForCountableFoods() {
		val brioche = IngredientLineParser.parse("eight brioche")
		brioche.quantity shouldBe BigDecimal("8")
		brioche.unit shouldBe "piece"
		brioche.parsedName shouldBe "brioche"
		brioche.isMeasurable shouldBe true
	}

	@Test
	fun parseDefaultsProduceNutsBreadCheeseAndMidphraseCuts() {
		val grapes = IngredientLineParser.parse("grapes")
		grapes.unit shouldBe "piece"
		grapes.isMeasurable shouldBe true

		val apples = IngredientLineParser.parse("granny smith apples")
		apples.unit shouldBe "piece"
		apples.isMeasurable shouldBe true

		val cashews = IngredientLineParser.parse("cashews")
		cashews.unit shouldBe "tbsp"
		cashews.isMeasurable shouldBe true

		val feta = IngredientLineParser.parse("feta cheese")
		feta.unit shouldBe "piece"
		feta.isMeasurable shouldBe true

		val bread = IngredientLineParser.parse("crusty bread")
		bread.unit shouldBe "piece"
		bread.isMeasurable shouldBe true

		val tea = IngredientLineParser.parse("black tea")
		tea.unit shouldBe "cup"
		tea.isMeasurable shouldBe true

		val espresso = IngredientLineParser.parse("brewed shot espresso")
		espresso.unit shouldBe "tbsp"
		espresso.parsedName shouldBe "brewed espresso"
		espresso.isMeasurable shouldBe true

		val catfish = IngredientLineParser.parse("catfish fillets along natural seam")
		catfish.unit shouldBe "piece"
		catfish.parsedName shouldBe "catfish"
		catfish.isMeasurable shouldBe true

		val filetMignon = IngredientLineParser.parse("center filets mignon")
		filetMignon.unit shouldBe "piece"
		filetMignon.parsedName shouldBe "filet mignon"
		filetMignon.isMeasurable shouldBe true

		val chickenBoiled = IngredientLineParser.parse("chicken boiled")
		chickenBoiled.unit shouldBe "piece"
		chickenBoiled.isMeasurable shouldBe true

		val tenderloins = IngredientLineParser.parse("pork tenderloins")
		tenderloins.unit shouldBe "piece"
		tenderloins.isMeasurable shouldBe true

		val ham = IngredientLineParser.parse("ham")
		ham.unit shouldBe "piece"
		ham.isMeasurable shouldBe true

		val wholeClove = IngredientLineParser.parse("whole clove")
		wholeClove.unit shouldBe "tsp"
		wholeClove.isMeasurable shouldBe true

		val allspice = IngredientLineParser.parse("whole allspice berries")
		allspice.unit shouldBe "tsp"
		allspice.isMeasurable shouldBe true

		val couscous = IngredientLineParser.parse("couscous")
		couscous.unit shouldBe "cup"
		couscous.isMeasurable shouldBe true

		val soda = IngredientLineParser.parse("soda water")
		soda.unit shouldBe "cup"
		soda.isMeasurable shouldBe true
	}
}

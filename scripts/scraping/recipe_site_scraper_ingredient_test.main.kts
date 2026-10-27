#!/usr/bin/env kotlin

@file:Import("ScrapedIngredientLines.kt")

// cache-bust: alt-or-incomplete-v18

data class IngredientTestCase(
	val description: String,
	val input: String,
	val expected: String?,
)

data class SplitTestCase(
	val description: String,
	val input: String,
	val expected: List<String>,
)

fun assertEquals(description: String, expected: String?, actual: String?) {
	if (expected != actual) {
		error("FAIL [$description]: expected '$expected', got '$actual'")
	}
}

fun assertSplitEquals(description: String, expected: List<String>, actual: List<String>) {
	if (expected != actual) {
		error("FAIL [$description]: expected $expected, got $actual")
	}
}

fun runSanitizeTests(cases: List<IngredientTestCase>) {
	cases.forEach { case ->
		val actual = sanitizeIngredientLine(case.input)?.text
		assertEquals(case.description, case.expected, actual)
	}
}

fun runNormalizeIngredientTextTests(cases: List<IngredientTestCase>) {
	cases.forEach { case ->
		val actual = normalizeIngredientText(case.input)
		assertEquals("normalizeIngredientText: ${case.description}", case.expected, actual)
	}
}

fun runSplitTests(cases: List<SplitTestCase>) {
	cases.forEach { case ->
		val actual = splitIngredientLine(case.input)
			.mapNotNull { sanitizeIngredientLine(it)?.text }
		assertSplitEquals(case.description, case.expected, actual)
	}
}

val sanitizeCases = listOf(
	IngredientTestCase(
		description = "adds space between grams and quantity",
		input = "60g Gruyère",
		expected = "60 g Gruyère",
	),
	IngredientTestCase(
		description = "adds space between tbsp and quantity",
		input = "2tbsp Chipotle Paste",
		expected = "2 tbsp Chipotle Paste",
	),
	IngredientTestCase(
		description = "leaves already spaced quantity unchanged",
		input = "2 tbsp olive oil",
		expected = "2 tbsp olive oil",
	),
	IngredientTestCase(
		description = "leaves non-measurement ingredients unchanged",
		input = "6 carrots",
		expected = "6 carrots",
	),
	IngredientTestCase(
		description = "adds space for fractional cup quantities",
		input = "1/2cup flour",
		expected = "1/2 cup flour",
	),
	IngredientTestCase(
		description = "adds space for milliliter quantities",
		input = "500ml water",
		expected = "500 ml water",
	),
	IngredientTestCase(
		description = "adds space before trailing punctuation",
		input = "250g, peeled and diced onions",
		expected = "250 g, peeled and diced onions",
	),
	IngredientTestCase(
		description = "fixes multiple measurements in one line",
		input = "60g butter and 30g sugar",
		expected = "60 g butter and 30 g sugar",
	),
	IngredientTestCase(
		description = "filters heading-like lines",
		input = "For the sauce:",
		expected = null,
	),
	IngredientTestCase(
		description = "filters solution for boiling heading",
		input = "Honey Solution for Boiling",
		expected = null,
	),
	IngredientTestCase(
		description = "filters optional percentage lines",
		input = "optional 0.34% ground cardamom, cinnamon, or ginger",
		expected = null,
	),
	IngredientTestCase(
		description = "filters all of the assembly lines",
		input = "All of the sweet stiff levain",
		expected = null,
	),
	IngredientTestCase(
		description = "strips trailing asterisk note marker",
		input = "1/2 tsp ground mahlep (1-2 tsp) *",
		expected = "1/2 tsp ground mahlep (1-2 tsp)",
	),
	IngredientTestCase(
		description = "filters equipment-like lines",
		input = "Large mixing bowl",
		expected = null,
	),
	IngredientTestCase(
		description = "restores float artifact one third to fraction",
		input = "0.33333334326744 cup strong brewed coffee or espresso",
		expected = "1/3 cup strong brewed coffee or espresso",
	),
	IngredientTestCase(
		description = "restores float artifact two thirds to fraction",
		input = "0.666666668653488 cup all-purpose flour",
		expected = "2/3 cup all-purpose flour",
	),
	IngredientTestCase(
		description = "restores simple imperial decimals to fractions",
		input = "0.75 teaspoon ground mace",
		expected = "3/4 teaspoon ground mace",
	),
	IngredientTestCase(
		description = "restores mixed number decimals to fractions",
		input = "1.5 cup chopped nuts",
		expected = "1 1/2 cup chopped nuts",
	),
	IngredientTestCase(
		description = "leaves non-standard decimals unchanged",
		input = "1.7 cups water",
		expected = "1.7 cups water",
	),
	IngredientTestCase(
		description = "leaves existing fraction notation unchanged",
		input = "2/3 cup sugar",
		expected = "2/3 cup sugar",
	),
	IngredientTestCase(
		description = "adds space before everyday capitalized units",
		input = "12Rasher smoked streaky bacon",
		expected = "12 Rasher smoked streaky bacon",
	),
	IngredientTestCase(
		description = "adds space before everyday unit words",
		input = "1Knob butter",
		expected = "1 Knob butter",
	),
	IngredientTestCase(
		description = "adds space before bulb sprig and stalk units",
		input = "1Bulb garlic, 3Sprig thyme and 3Stalk celery",
		expected = "1 Bulb garlic, 3 Sprig thyme and 3 Stalk celery",
	),
	IngredientTestCase(
		description = "adds space before bag and handful units",
		input = "3Bag spinach and 1Handful parsley",
		expected = "3 Bag spinach and 1 Handful parsley",
	),
	IngredientTestCase(
		description = "adds space before metric length units",
		input = "3cm piece ginger",
		expected = "3 cm piece ginger",
	),
	IngredientTestCase(
		description = "does not split quantity from ingredient name without boundary",
		input = "250gonions, diced",
		expected = "250gonions, diced",
	),
	IngredientTestCase(
		description = "filters dough conditioners like ascorbic acid / vitamin c",
		input = "OPTIONAL: 1 pinch ascorbic acid (Vitamin C)",
		expected = null,
	),
	IngredientTestCase(
		description = "parses optional ingredient with quantity as optional ingredient",
		input = "OPTIONAL: 1 pinch salt",
		expected = "1 pinch salt",
	),
	IngredientTestCase(
		description = "filters first dough structural heading",
		input = "First Dough",
		expected = null,
	),
	IngredientTestCase(
		description = "filters second dough structural heading",
		input = "Second Dough",
		expected = null,
	),
	IngredientTestCase(
		description = "filters lye bath structural heading",
		input = "Lye Bath",
		expected = null,
	),
	IngredientTestCase(
		description = "filters timing and scale instruction notes",
		input =
			"~36 hours Time(Note that volumetric measures are inherently " +
				"inaccurate - especially for flour where 1 cup is going to be " +
				"a very different amount of flour for you and for me and even " +
				"for you one time vs. another time. You should get a scale.)",
		expected = null,
	),
	IngredientTestCase(
		description = "filters baker percentages note",
		input = "Baker's Percentages",
		expected = null,
	),
	IngredientTestCase(
		description = "strips prep label from optional ingredient",
		input = "optional egg wash: 1 egg beaten",
		expected = "1 egg beaten",
	),
	IngredientTestCase(
		description = "drops scaled dough heading",
		input = "Dough for 4 breakfast pizzas * see notes for scaling tips",
		expected = null,
	),
)

data class AlternativeTestCase(
	val description: String,
	val input: String,
	val expected: List<String>,
)

fun runAlternativeTests(cases: List<AlternativeTestCase>) {
	cases.forEach { case ->
		val actual = sanitizeIngredientLine(case.input)
			?.let(::expandProcessedAlternatives)
			?.map { ingredient -> ingredient.text }
		if (actual != case.expected) {
			error("FAIL [${case.description}]: expected ${case.expected}, got $actual")
		}
	}
}

val alternativeCases = listOf(
	AlternativeTestCase(
		description = "parses parsley or tarragon",
		input = "parsley or tarragon",
		expected = listOf("parsley", "tarragon"),
	),
	AlternativeTestCase(
		description = "parses shared quantity prefix for alternatives",
		input = "2 tbsp parsley or fresh tarragon",
		expected = listOf("2 tbsp parsley", "2 tbsp fresh tarragon"),
	),
	AlternativeTestCase(
		description = "keeps shared-head mini or regular chocolate chips together",
		input = "2 tablespoons mini or regular chocolate chips, optional",
		expected = listOf("2 tablespoons mini or regular chocolate chips, optional"),
	),
	AlternativeTestCase(
		description = "keeps incomplete shredded poached or chicken together",
		input = "1 1/2 pounds shredded poached or 1 store-bought rotisserie chicken meat",
		expected = listOf(
			"1 1/2 pounds shredded poached or 1 store-bought rotisserie chicken meat",
		),
	),
	AlternativeTestCase(
		description = "keeps plum or Roma tomatoes together",
		input = "4 plum or Roma tomatoes (about 442 g), halved",
		expected = listOf("4 plum or Roma tomatoes (about 442 g), halved"),
	),
	AlternativeTestCase(
		description = "keeps Strong 00 or type 0 flour together",
		input = "50 g Strong 00 or type 0 flour",
		expected = listOf("50 g Strong 00 or type 0 flour"),
	),
	AlternativeTestCase(
		description = "keeps Bottled mineral or spring water together",
		input = "Bottled mineral or spring water (enough to cover fruit)",
		expected = listOf("Bottled mineral or spring water (enough to cover fruit)"),
	),
	AlternativeTestCase(
		description = "keeps White or apple cider vinegar together",
		input = "2 tbsp White or apple cider vinegar",
		expected = listOf("2 tbsp White or apple cider vinegar"),
	),
	AlternativeTestCase(
		description = "keeps Chocolate Chips or Chunks together",
		input = "170 g Chocolate Chips or Chunks",
		expected = listOf("170 g Chocolate Chips or Chunks"),
	),
	AlternativeTestCase(
		description = "keeps Smooth strawberry juice or puree together",
		input = "4 tbsp Smooth strawberry juice or puree",
		expected = listOf("4 tbsp Smooth strawberry juice or puree"),
	),
	AlternativeTestCase(
		description = "keeps finely minced or grated garlic together",
		input = "3 Garlic cloves, finely minced or grated",
		expected = listOf("3 Garlic cloves, finely minced or grated"),
	),
	AlternativeTestCase(
		description = "keeps rolled or flaked oat groats together",
		input = "83 grams rolled or flaked oat groats (1 cup)",
		expected = listOf("83 grams rolled or flaked oat groats (1 cup)"),
	),
	AlternativeTestCase(
		description = "keeps psyllium husk powder or flakes together",
		input = "12 grams psyllium husk powder or flakes (2 Tbsp)",
		expected = listOf("12 grams psyllium husk powder or flakes (2 Tbsp)"),
	),
	AlternativeTestCase(
		description = "keeps tomatoes to cut in half or grate together",
		input = "1-2 juicy, sweet-acidic tomatoes to cut in half or grate",
		expected = listOf("1-2 juicy, sweet-acidic tomatoes to cut in half or grate"),
	),
	AlternativeTestCase(
		description = "keeps quantity specification with or more intact",
		input = "75 grams or more dried currants or raisins (1/2 cup)",
		expected = listOf("75 grams or more dried currants or raisins (1/2 cup)"),
	),
	AlternativeTestCase(
		description = "expands light olive or avocado oil with shared noun suffix",
		input = "65 grams light olive or avocado oil (1/3 cup)",
		expected = listOf(
			"65 grams light olive oil (1/3 cup)",
			"65 grams avocado oil (1/3 cup)",
		),
	),
	AlternativeTestCase(
		description = "keeps Extra dark rye or rice flour together",
		input = "Extra dark rye or rice flour (for dusting)",
		expected = listOf("Extra dark rye or rice flour (for dusting)"),
	),
	AlternativeTestCase(
		description = "keeps wholemeal or seven-grain bread together",
		input = "8–10 slices of wholemeal or seven-grain bread",
		expected = listOf("8–10 slices of wholemeal or seven-grain bread"),
	),
	AlternativeTestCase(
		description = "keeps conversational semicolon alternative list together",
		input = "260-520 grams flour, ideally rye or whole wheat; all purpose or bread flour are fine (2-4 cups)",
		expected = listOf(
			"260-520 grams flour, ideally rye or whole wheat; all purpose or bread flour are fine (2-4 cups)",
		),
	),
	AlternativeTestCase(
		description = "trims hyphens surrounding or in alternative",
		input = "240 g high gluten flour - or - bread flour with added vital wheat gluten",
		expected = listOf(
			"240 g high gluten flour",
			"240 g bread flour with added vital wheat gluten",
		),
	),
)

val normalizeTextCases = listOf(
	IngredientTestCase(
		description = "parenthesizes soaked-in clause",
		input = "80g raisins, soaked in 120 ml water - or - 60 ml water + 60 ml rum",
		expected = "80 g raisins (soaked in 120 ml water - or - 60 ml water + 60 ml rum)",
	),
	IngredientTestCase(
		description = "adds space between grams and quantity",
		input = "60g Gruyère",
		expected = "60 g Gruyère",
	),
	IngredientTestCase(
		description = "restores float artifact two thirds to fraction",
		input = "0.666666668653488 cup all-purpose flour",
		expected = "2/3 cup all-purpose flour",
	),
	IngredientTestCase(
		description = "leaves heading-like text unchanged",
		input = "For the sauce:",
		expected = "For the sauce:",
	),
	IngredientTestCase(
		description = "strips trailing Sainsbury's brand",
		input = "250g bag young spinach by Sainsbury's",
		expected = "250 g bag young spinach",
	),
	IngredientTestCase(
		description = "strips curly-apostrophe Sainsbury’s brand",
		input = "250 g pack closed cup chestnut mushrooms by Sainsbury’s, quartered",
		expected = "250 g pack closed cup chestnut mushrooms, quartered",
	),
	IngredientTestCase(
		description = "leaves trailing list commas untouched without brands",
		input = "14 ounces similar dark greens, such as beet greens, swiss chard,",
		expected = "14 ounces similar dark greens, such as beet greens, swiss chard,",
	),
	IngredientTestCase(
		description = "strips Tesco brand suffix",
		input = "1 tin chopped tomatoes Tesco",
		expected = "1 tin chopped tomatoes",
	),
	IngredientTestCase(
		description = "strips bare Sainsbury leftover token",
		input = "extra virgin olive oil sainsbury",
		expected = "extra virgin olive oil",
	),
)

val splitCases = listOf(
	SplitTestCase(
		description = "splits concatenated ingredients and adds spaces",
		input = "flour60g butter2tbsp",
		expected = listOf("flour", "60 g butter", "2 tbsp"),
	),
	SplitTestCase(
		description = "joins trailing or with the next line",
		input = "1 pound melting cheese such as queso Chihuahua or\nMozzarella, shredded",
		expected = listOf(
			"1 pound melting cheese such as queso Chihuahua or Mozzarella, shredded",
		),
	),
)

fun runGroupProcessingTests() {
	val input = listOf(
		ProcessedScrapedIngredient("Dough for 4 breakfast pizzas * see notes for scaling tips"),
		ProcessedScrapedIngredient("300 grams flour"),
		ProcessedScrapedIngredient("First Dough"),
		ProcessedScrapedIngredient("240 g high gluten flour"),
		ProcessedScrapedIngredient("Second Dough"),
		ProcessedScrapedIngredient("All of the First Dough"),
		ProcessedScrapedIngredient("OPTIONAL: 1 pinch salt"),
		ProcessedScrapedIngredient(
			"~36 hours Time(Note that volumetric measures are inherently inaccurate - you should get a scale.)",
		),
		ProcessedScrapedIngredient("YEAST"),
		ProcessedScrapedIngredient("3 grams instant yeast (1 tsp)"),
	)
	val groups = processStoredScrapedIngredientGroups(null, input)
	if (groups.size != 4) {
		error("Expected 4 groups, got ${groups.size}")
	}
	if (groups[0].first != "Dough for 4 breakfast pizzas") {
		error("Expected group 0 name 'Dough for 4 breakfast pizzas', got '${groups[0].first}'")
	}
	if (groups[0].second.map { it.text } != listOf("300 grams flour")) {
		error("Unexpected group 0 items: ${groups[0].second}")
	}
	if (groups[1].first != "First Dough") {
		error("Expected group 1 name 'First Dough', got '${groups[1].first}'")
	}
	if (groups[1].second.map { it.text } != listOf("240 g high gluten flour")) {
		error("Unexpected group 1 items: ${groups[1].second}")
	}
	if (groups[2].first != "Second Dough") {
		error("Expected group 2 name 'Second Dough', got '${groups[2].first}'")
	}
	val group2Texts = groups[2].second.map { it.text }
	if (group2Texts != listOf("1 pinch salt")) {
		error("Unexpected group 2 items: ${groups[2].second}")
	}
	val saltReq = groups[2].second.last().requirement
	if (saltReq != "OPTIONAL") {
		error("Expected salt requirement 'OPTIONAL', got '$saltReq'")
	}
	if (groups[3].first != "YEAST") {
		error("Expected group 3 name 'YEAST', got '${groups[3].first}'")
	}
	if (groups[3].second.map { it.text } != listOf("3 grams instant yeast (1 tsp)")) {
		error("Unexpected group 3 items: ${groups[3].second}")
	}
}

fun main() {
	runSanitizeTests(sanitizeCases)
	runNormalizeIngredientTextTests(normalizeTextCases)
	runSplitTests(splitCases)
	runAlternativeTests(alternativeCases)
	runGroupProcessingTests()
	println(
		"All ${sanitizeCases.size + normalizeTextCases.size + splitCases.size + alternativeCases.size + 1} " +
			"scraped ingredient line tests passed.",
	)
}

main()

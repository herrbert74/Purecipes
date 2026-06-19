#!/usr/bin/env kotlin

@file:Import("ScrapedIngredientLines.kt")

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
)

val normalizeTextCases = listOf(
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
)

val splitCases = listOf(
	SplitTestCase(
		description = "splits concatenated ingredients and adds spaces",
		input = "flour60g butter2tbsp",
		expected = listOf("flour", "60 g butter", "2 tbsp"),
	),
)

fun main() {
	runSanitizeTests(sanitizeCases)
	runNormalizeIngredientTextTests(normalizeTextCases)
	runSplitTests(splitCases)
	runAlternativeTests(alternativeCases)
	println(
		"All ${sanitizeCases.size + normalizeTextCases.size + splitCases.size + alternativeCases.size} " +
			"scraped ingredient line tests passed.",
	)
}

main()

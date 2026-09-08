package app.purecipes.backend.feature.nutrition

import java.text.Normalizer

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val SUCH_AS_CLAUSE = Regex(""",?\s*such as\b.*""", RegexOption.IGNORE_CASE)

	private val COMBINING_MARKS = Regex("\\p{M}+")

	private val preparationTokens = setOf(
		"beaten",
		"chilled",
		"chopped",
		"coarsely",
		"cold",
		"concentrated",
		"cored",
		"crushed",
		"cubed",
		"cut",
		"deseeded",
		"diced",
		"divided",
		"drained",
		"dried",
		"finely",
		"fresh",
		"freshly",
		"grated",
		"ground",
		"halved",
		"homemade",
		"juiced",
		"large",
		"leaves",
		"lightly",
		"medium",
		"melted",
		"minced",
		"needed",
		"note",
		"notes",
		"peeled",
		"pitted",
		"preferably",
		"quartered",
		"rinsed",
		"room",
		"roughly",
		"scrubbed",
		"see",
		"seeded",
		"separated",
		"shredded",
		"sliced",
		"slightly",
		"smashed",
		"smoked",
		"softened",
		"squeezed",
		"stemmed",
		"stems",
		"temperature",
		"thinly",
		"torn",
		"trimmed",
		"unpeeled",
		"zested",
	)

	private val measureLeftoverTokens = setOf(
		"about",
		"and",
		"approx",
		"approximately",
		"each",
		"fluid",
		"gram",
		"grams",
		"kilo",
		"kilogram",
		"kilograms",
		"liter",
		"liters",
		"litre",
		"litres",
		"milliliter",
		"milliliters",
		"millilitre",
		"millilitres",
		"only",
		"or",
		"ounce",
		"ounces",
		"plus",
		"pound",
		"pounds",
		"tender",
		"total",
		"weight",
		"with",
	)

	private val fillerTokens = setOf(
		"dredging",
		"drizzling",
		"dusting",
		"for",
		"frying",
		"garnish",
		"into",
		"more",
		"optional",
		"piece",
		"pieces",
		"serve",
		"serving",
		"surface",
		"taste",
	)

	private val sizeOnlyTokens = setOf(
		"extra",
		"extra-large",
		"jumbo",
		"large",
		"medium",
		"small",
	)

	fun normalize(value: String): String {
		val ascii = COMBINING_MARKS.replace(
			Normalizer.normalize(value.lowercase(), Normalizer.Form.NFKD),
			"",
		)
		return NON_ALPHANUMERIC.replace(ascii, " ").trim()
	}

	fun tokens(normalized: String): List<String> =
		normalized.split(' ').filter { token -> token.length >= MIN_TOKEN_LENGTH }

	fun forLookup(value: String): String {
		val withoutClause = SUCH_AS_CLAUSE.replace(value.substringBefore(';').trim(), "").trim()
		val normalized = normalize(withoutClause)
		return tokens(normalized).filterNot { token ->
			token.all(Char::isDigit) ||
				token in preparationTokens ||
				token in measureLeftoverTokens ||
				token in fillerTokens
		}.joinToString(" ")
	}

	fun hasMeaningfulFoodName(value: String): Boolean {
		val lookup = forLookup(value)
		if (lookup.isNotBlank()) {
			return true
		}
		val normalizedTokens = tokens(normalize(value)).filterNot { token -> token.all(Char::isDigit) }
		return normalizedTokens.any { token -> token !in sizeOnlyTokens && token !in preparationTokens }
	}

	private const val MIN_TOKEN_LENGTH = 3
}

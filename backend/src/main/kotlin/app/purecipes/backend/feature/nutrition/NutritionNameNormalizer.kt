package app.purecipes.backend.feature.nutrition

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val SUCH_AS_CLAUSE = Regex(""",?\s*such as\b.*""", RegexOption.IGNORE_CASE)

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
		"halves",
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
		"removed",
		"rinsed",
		"room",
		"roughly",
		"scrubbed",
		"see",
		"seeded",
		"separated",
		"shredded",
		"sized",
		"sliced",
		"slices",
		"slightly",
		"smashed",
		"smoked",
		"softened",
		"squeezed",
		"stems",
		"temperature",
		"thick",
		"thinly",
		"thumb",
		"torn",
		"trimmed",
		"unpeeled",
		"whacked",
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
		"pieces",
		"serve",
		"serving",
		"surface",
		"taste",
	)

	fun normalize(value: String): String =
		NON_ALPHANUMERIC.replace(value.lowercase(), " ").trim()

	fun tokens(normalized: String): List<String> =
		normalized.split(' ').filter { token -> token.length >= MIN_TOKEN_LENGTH }

	fun forLookup(value: String): String {
		val withoutClause = SUCH_AS_CLAUSE.replace(value.substringBefore(';').trim(), "").trim()
		val normalized = normalize(withoutClause)
		val lookupTokens = tokens(normalized).filterNot { token ->
			token in preparationTokens || token in measureLeftoverTokens || token in fillerTokens
		}
		return lookupTokens.joinToString(" ").ifBlank { normalized }
	}

	private const val MIN_TOKEN_LENGTH = 3
}

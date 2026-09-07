package app.purecipes.backend.feature.nutrition

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val preparationTokens = setOf(
		"beaten",
		"chopped",
		"coarsely",
		"cored",
		"crushed",
		"cubed",
		"diced",
		"divided",
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
		"lightly",
		"medium",
		"melted",
		"minced",
		"peeled",
		"quartered",
		"room",
		"roughly",
		"seeded",
		"shredded",
		"sliced",
		"smashed",
		"smoked",
		"softened",
		"squeezed",
		"temperature",
		"thinly",
		"torn",
	)

	fun normalize(value: String): String =
		NON_ALPHANUMERIC.replace(value.lowercase(), " ").trim()

	fun tokens(normalized: String): List<String> =
		normalized.split(' ').filter { token -> token.length >= MIN_TOKEN_LENGTH }

	fun forLookup(value: String): String {
		val withoutClause = value.substringBefore(';').trim()
		val normalized = normalize(withoutClause)
		val lookupTokens = tokens(normalized).filterNot { token -> token in preparationTokens }
		return lookupTokens.joinToString(" ").ifBlank { normalized }
	}

	private const val MIN_TOKEN_LENGTH = 3
}

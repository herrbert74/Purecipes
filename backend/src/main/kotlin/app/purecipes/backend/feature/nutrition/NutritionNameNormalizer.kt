package app.purecipes.backend.feature.nutrition

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val preparationTokens = setOf(
		"chopped",
		"diced",
		"minced",
		"sliced",
		"crushed",
		"grated",
		"peeled",
		"cored",
		"seeded",
		"halved",
		"quartered",
		"cubed",
		"shredded",
		"torn",
		"roughly",
		"finely",
		"thinly",
		"coarsely",
		"fresh",
		"freshly",
		"large",
		"small",
		"medium",
		"ground",
		"smoked",
		"dried",
		"divided",
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

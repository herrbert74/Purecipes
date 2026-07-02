package app.purecipes.backend.feature.nutrition

internal data class ParsedServings(
	val count: Double,
	val description: String,
)

internal object ServingsParser {

	private val SERVING_COUNT_REGEX = Regex(
		pattern = """(\d+(?:\.\d+)?)\s*(?:servings?|serves|portions?|people)""",
		option = RegexOption.IGNORE_CASE,
	)

	private val LEADING_COUNT_REGEX = Regex(
		pattern = """^(\d+(?:\.\d+)?)\s*(?:x\s*)?(?:servings?|serves)""",
		option = RegexOption.IGNORE_CASE,
	)

	fun parse(yields: String?): ParsedServings? {
		val trimmed = yields?.trim().orEmpty()
		if (trimmed.isEmpty()) {
			return null
		}

		val match = SERVING_COUNT_REGEX.find(trimmed) ?: LEADING_COUNT_REGEX.find(trimmed)
		val count = match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
		return if (count == null || count <= 0.0) {
			null
		} else {
			ParsedServings(
				count = count,
				description = trimmed,
			)
		}
	}
}

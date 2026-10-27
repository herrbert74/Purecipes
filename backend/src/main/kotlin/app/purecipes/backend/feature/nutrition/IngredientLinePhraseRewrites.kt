package app.purecipes.backend.feature.nutrition

internal object IngredientLinePhraseRewrites {

	private const val REGEX_GROUP_FIRST = 1
	private const val REGEX_GROUP_SECOND = 2

	private val leadingInstructionPattern = Regex(
		"""^to\s+(?:brush|sprinkle|top|dust|coat)\b[^,]*,\s*(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val sizeAlternativePattern = Regex(
		"""^(\d+)\s+(?:large|medium|small)\s+or\s+\d+\s+(?:large|medium|small)\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val slicedFruitPattern = Regex(
		"""^(?:sliced\s+fruit(?:\s+of\s+your\s+choosing)?)$""",
		RegexOption.IGNORE_CASE,
	)

	fun rewrite(value: String): String? {
		val trimmed = value.trim()
		val instruction = leadingInstructionPattern.matchEntire(trimmed)
		val sizeAlternative = sizeAlternativePattern.matchEntire(trimmed)
		val slicedFruit = slicedFruitPattern.matchEntire(trimmed)
		return when {
			instruction != null -> instruction.groupValues[REGEX_GROUP_FIRST].trim()
			sizeAlternative != null -> listOf(
				sizeAlternative.groupValues[REGEX_GROUP_FIRST],
				sizeAlternative.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			slicedFruit != null -> "1 cup $trimmed"

			else -> null
		}
	}
}

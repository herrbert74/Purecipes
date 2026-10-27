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

	private val countWithPerItemWeightPattern = Regex(
		"""^(\d+)\s+(pigeons?)\s*\((?:about|approx\.?)\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s+each\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val countableJuniperBerriesPattern = Regex(
		"""^\d+\s+juniper\s+berries$""",
		RegexOption.IGNORE_CASE,
	)

	private val countableCapersPattern = Regex(
		"""^\d+\s+capers$""",
		RegexOption.IGNORE_CASE,
	)

	fun rewrite(value: String): String? {
		val trimmed = value.trim()
		val instruction = leadingInstructionPattern.matchEntire(trimmed)
		val sizeAlternative = sizeAlternativePattern.matchEntire(trimmed)
		val slicedFruit = slicedFruitPattern.matchEntire(trimmed)
		val perItemRewrite = countWithPerItemWeightPattern.matchEntire(trimmed)?.let { formatPerItemWeight(it) }
		return when {
			perItemRewrite != null -> perItemRewrite
			countableJuniperBerriesPattern.matches(trimmed) -> "1 tsp juniper berries"
			countableCapersPattern.matches(trimmed) -> "1 tbsp capers"
			instruction != null -> instruction.groupValues[REGEX_GROUP_FIRST].trim()
			sizeAlternative != null -> listOf(
				sizeAlternative.groupValues[REGEX_GROUP_FIRST],
				sizeAlternative.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			slicedFruit != null -> "1 cup $trimmed"

			else -> null
		}
	}

	private fun formatPerItemWeight(match: MatchResult): String? {
		val count = match.groupValues[REGEX_GROUP_FIRST].toDoubleOrNull()
		val weight = match.groupValues[3].toDoubleOrNull()
		val unit = match.groupValues[4]
		val item = match.groupValues[REGEX_GROUP_SECOND].trim()
		return if (count != null && weight != null) {
			"${(count * weight).toInt()} $unit $item"
		} else {
			null
		}
	}
}

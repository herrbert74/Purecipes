package app.purecipes.feature.cooking.ui

internal object CookingStepHighlightParser {

	private val rangeDurationRegex = Regex(
		pattern = """(?i)(\d+)\s*(?:-|–|to)\s*(\d+)\s*(hours?|hrs?|h|minutes?|mins?|min|seconds?|secs?|sec)\b""",
	)

	private val singleDurationRegex = Regex(
		pattern = """(?i)(\d+)\s*(hours?|hrs?|h|minutes?|mins?|min|seconds?|secs?|sec)\b""",
	)

	private val temperatureRegex = Regex(
		pattern = """(?i)(\d+)\s*°\s*([CF])\b|(?i)(\d+)\s*degrees?\s*([CF])\b""",
	)

	fun parse(step: String): List<CookingStepHighlight> {
		val rangeDurations = rangeDurationRegex.findAll(step).mapNotNull { match ->
			val startAmount = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
			val endAmount = match.groupValues[2].toIntOrNull() ?: return@mapNotNull null
			CookingStepHighlight.Duration(
				startIndex = match.range.first,
				endIndex = match.range.last + 1,
				text = match.value,
				totalSeconds = toSeconds(maxOf(startAmount, endAmount), match.groupValues[3]),
			)
		}
		val singleDurations = singleDurationRegex.findAll(step).mapNotNull { match ->
			if (rangeDurations.any { it.overlaps(match.range) }) {
				return@mapNotNull null
			}
			val amount = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
			CookingStepHighlight.Duration(
				startIndex = match.range.first,
				endIndex = match.range.last + 1,
				text = match.value,
				totalSeconds = toSeconds(amount, match.groupValues[2]),
			)
		}
		val temperatures = temperatureRegex.findAll(step).map { match ->
			CookingStepHighlight.Temperature(
				startIndex = match.range.first,
				endIndex = match.range.last + 1,
				text = match.value,
			)
		}
		return (rangeDurations + singleDurations + temperatures)
			.sortedBy { it.startIndex }
			.toList()
			.filterOverlaps()
	}

	private fun CookingStepHighlight.overlaps(range: IntRange): Boolean =
		startIndex <= range.last && endIndex > range.first

	private fun toSeconds(amount: Int, unit: String): Int {
		val normalized = unit.lowercase()
		return when {
			normalized.startsWith("h") -> amount * SECONDS_PER_HOUR
			normalized.startsWith("m") -> amount * SECONDS_PER_MINUTE
			else -> amount
		}
	}

	private fun List<CookingStepHighlight>.filterOverlaps(): List<CookingStepHighlight> {
		if (isEmpty()) return this
		val result = mutableListOf<CookingStepHighlight>()
		for (highlight in this) {
			val previous = result.lastOrNull()
			if (previous == null || highlight.startIndex >= previous.endIndex) {
				result += highlight
			}
		}
		return result
	}

	private const val SECONDS_PER_MINUTE = 60
	private const val SECONDS_PER_HOUR = 60 * SECONDS_PER_MINUTE
}

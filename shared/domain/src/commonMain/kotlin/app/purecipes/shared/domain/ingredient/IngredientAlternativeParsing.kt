package app.purecipes.shared.domain.ingredient

internal object IngredientAlternativeParsing {

	private const val ALTERNATIVE_SEPARATOR = " or "
	private const val ALTERNATIVE_SEPARATOR_LENGTH = 4

	private val quantityPrefixRegex = Regex(
		pattern = """^((?:\d+(?:\.\d+)?|\d+\s+\d+/\d+|\d+/\d+)(?:\s+\w+)?(?:\s+of)?\s+)""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val ofPrefixRegex = Regex(
		pattern = """^(.*\bof)\s+""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val trailingParentheticalSuffixRegex = Regex(
		pattern = """\s*\([^)]+\)\s*$""",
	)

	fun splitAlternativeParts(text: String): List<String> {
		if (!containsAlternativeSeparator(text)) {
			return listOf(text)
		}

		val parts = mutableListOf<String>()
		var depth = 0
		var start = 0
		var index = 0
		while (index < text.length) {
			when (text[index]) {
				'(' -> depth++
				')' -> if (depth > 0) {
					depth--
				}
			}
			if (
				depth == 0 &&
					text.regionMatches(
						index,
						ALTERNATIVE_SEPARATOR,
						0,
						ALTERNATIVE_SEPARATOR_LENGTH,
						ignoreCase = true,
					)
			) {
				parts += text.substring(start, index).trim()
				index += ALTERNATIVE_SEPARATOR_LENGTH
				start = index
				continue
			}
			index++
		}
		parts += text.substring(start).trim()
		return parts.filter { part -> part.isNotBlank() }
	}

	fun expandAlternativeParts(parts: List<String>): List<String> {
		if (parts.size <= 1) {
			return parts
		}
		val firstAlternative = parts.first()
		val expanded = parts.mapIndexed { partIndex, part ->
			if (partIndex == 0) {
				part.trim()
			} else {
				expandWithSharedPrefix(firstAlternative, part)
			}
		}
		val sharedSuffix = trailingParentheticalSuffixRegex.find(parts.last())?.value
		return if (sharedSuffix != null && !firstAlternative.contains('(')) {
			expanded.mapIndexed { partIndex, text ->
				if (partIndex == 0) {
					text + sharedSuffix
				} else {
					text
				}
			}
		} else {
			expanded
		}
	}

	private fun containsAlternativeSeparator(text: String): Boolean {
		var depth = 0
		var index = 0
		while (index < text.length) {
			when (text[index]) {
				'(' -> depth++
				')' -> if (depth > 0) {
					depth--
				}
			}
			if (
				depth == 0 &&
					text.regionMatches(
						index,
						ALTERNATIVE_SEPARATOR,
						0,
						ALTERNATIVE_SEPARATOR_LENGTH,
						ignoreCase = true,
					)
			) {
				return true
			}
			index++
		}
		return false
	}

	private fun expandWithSharedPrefix(firstAlternative: String, part: String): String {
		val trimmed = part.trim()
		if (trimmed.firstOrNull()?.isDigit() == true) {
			return trimmed
		}

		val quantityPrefix = quantityPrefixRegex.find(firstAlternative)?.value
		return when {
			quantityPrefix != null -> quantityPrefix + trimmed
			else -> {
				val ofPrefix = ofPrefixRegex.find(firstAlternative)?.groupValues?.get(1)
				if (ofPrefix != null) {
					"$ofPrefix $trimmed"
				} else {
					trimmed
				}
			}
		}
	}
}

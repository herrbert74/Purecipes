package app.purecipes.shared.domain.ingredient

internal object IngredientAlternativeParsing {

	private const val ALTERNATIVE_SEPARATOR_OR = " or "
	private const val ALTERNATIVE_SEPARATOR_AND_OR = " and/or "

	private fun matchAlternativeSeparatorLength(text: String, index: Int): Int = when {
		text.regionMatches(
			index,
			ALTERNATIVE_SEPARATOR_AND_OR,
			0,
			ALTERNATIVE_SEPARATOR_AND_OR.length,
			ignoreCase = true,
		) -> ALTERNATIVE_SEPARATOR_AND_OR.length

		text.regionMatches(
			index,
			ALTERNATIVE_SEPARATOR_OR,
			0,
			ALTERNATIVE_SEPARATOR_OR.length,
			ignoreCase = true,
		) -> ALTERNATIVE_SEPARATOR_OR.length

		else -> 0
	}

	private const val PACKAGING_CONTAINER_PATTERN =
		"""(?:can|cans|tin|tins|jar|jars|bottle|bottles|bag|bags|pack|packs|packet|packets|package|packages|""" +
			"""box|boxes|carton|cartons|tub|tubs|pouch|pouches|block|blocks)"""

	private const val LEADING_UNIT_PATTERN =
		"""(?:g|kg|ml|l|oz|lbs?|tsps?|tbsps?|teaspoons?|tablespoons?|cups?|pints?|quarts?|gallons?|""" +
			"""pinch(?:es)?|dash(?:es)?|handfuls?|bags?|cans?|tins?|packs?|packages?|packets?|jars?|bottles?|""" +
			"""slices?|rashers?|cloves?|glasses?|mugs?|stalks?|sprigs?|heads?|bunch(?:es)?|pieces?|""" +
			"""pounds?|ounces?|grams?|kilograms?|liters?|litres?|milliliters?|millilitres?)"""

	private val nonAlternativeOrPhraseRegexes = listOf(
		Regex(
			pattern = """\b$PACKAGING_CONTAINER_PATTERN\s+or\s+$PACKAGING_CONTAINER_PATTERN\b""",
			options = setOf(RegexOption.IGNORE_CASE),
		),
		Regex(
			pattern = """\b(?:splash|pinch|dash|drop|drizzle|handful|glug|knob|sprig|slice|clove|squeeze|""" +
				"""scoop|spoon|tbsp|tsp|tablespoon|teaspoon)\s+or\s+(?:two|2|three|3|so|more)\b""",
			options = setOf(RegexOption.IGNORE_CASE),
		),
		Regex(
			pattern = """\b(?:homemade\s+or\s+store-bought|homemade\s+or\s+low-sodium|canned\s+or\s+tinned|""" +
				"""fresh\s+or\s+dried|fresh\s+or\s+frozen|cut\s+or\s+torn|grated\s+or\s+shredded|""" +
				"""minced\s+or\s+pressed)\b""",
			options = setOf(RegexOption.IGNORE_CASE),
		),
	)

	private val keepSinglePhrases = listOf(
		"mortar and pestle",
		"shredded with a knife",
		"the equivalent of",
	)

	private val quantityPrefixRegex = Regex(
		pattern = """^((?:\d+(?:\.\d+)?|\d+\s+\d+/\d+|\d+/\d+)(?:\s+$LEADING_UNIT_PATTERN)?(?:\s+of)?\s+)""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val ofPrefixRegex = Regex(
		pattern = """^(.*\bof)\s+""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val trailingParentheticalSuffixRegex = Regex(
		pattern = """\s*\([^)]+\)\s*$""",
	)

	private fun findNonAlternativeOrRanges(text: String): List<IntRange> =
		nonAlternativeOrPhraseRegexes.flatMap { regex ->
			regex.findAll(text).map(MatchResult::range).toList()
		}

	fun splitAlternativeParts(text: String): List<String> {
		if (!containsAlternativeSeparator(text) || keepSinglePhrases.any { text.contains(it, ignoreCase = true) }) {
			return listOf(text)
		}

		val ignoredRanges = findNonAlternativeOrRanges(text)
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
			if (depth == 0 && ignoredRanges.none { range -> index in range }) {
				val separatorLength = matchAlternativeSeparatorLength(text, index)
				if (separatorLength > 0) {
					parts += text.substring(start, index).trim()
					index += separatorLength
					start = index
					continue
				}
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
		val ignoredRanges = findNonAlternativeOrRanges(text)
		var depth = 0
		var index = 0
		while (index < text.length) {
			when (text[index]) {
				'(' -> depth++
				')' -> if (depth > 0) {
					depth--
				}
			}
			val isTopLevel = depth == 0 && ignoredRanges.none { range -> index in range }
			if (isTopLevel && matchAlternativeSeparatorLength(text, index) > 0) {
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

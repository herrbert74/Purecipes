package app.purecipes.backend.feature.search

internal object IngredientHeadingRules {

	private const val MIN_ASTERISK_HEADING_WORDS = 2

	private const val MIN_UPPERCASE_HEADING_WORDS = 2

	private val percentLinePattern = Regex("""^\d+(?:\.\d+)?\s*%""")

	private val parentheticalPattern = Regex("""\([^)]*\)""")

	private val asteriskClausePattern = Regex("""\*.*$""")

	private val whitespacePattern = Regex("""\s+""")

	private val headingLinePrefixes = listOf(
		"dough for",
		"pre-dough",
	)

	private val noteLinePrefixes = listOf(
		"baker's percent",
		"baker’s percent",
		"bakers percent",
		"baker percent",
		"ingredient percentages",
		"see the notes",
		"see notes",
		"see below",
		"if possible",
		"the volume amounts",
		"volume measurements",
		"these recipes were",
		"(reduce",
		"chestnut meal,",
		"chestnut flour and meal,",
		"dried fruit and candied peel alternative",
		"honey solution",
	)

	private val linePrefixes = headingLinePrefixes + noteLinePrefixes

	private val lineClauses = listOf(
		"approximations",
		"use a scale",
		"kitchen scale",
		"volumetric measures",
	)

	private val exactLines = setOf(
		"aromatic mix",
		"batter",
		"crumble",
		"custard",
		"inclusions",
		"oatmeal",
		"pate",
		"pâté",
		"rye sourdough",
		"scald",
		"slaw",
		"sweet stiff levain",
	)

	private val headingLastWords = setOf(
		"additions",
		"bath",
		"boule",
		"conchas",
		"crumble",
		"custard",
		"ferment",
		"filling",
		"ingredients",
		"inclusions",
		"percentages",
		"poolish",
		"porridge",
		"topping",
		"toppings",
		"wash",
	)

	private val doughHeadingLastWords = setOf(
		"batter",
		"dough",
	)

	private val doughHeadingFirstWords = setOf(
		"batter",
		"brioche",
		"dough",
		"donut",
		"final",
		"first",
		"main",
		"second",
	)

	fun isSectionHeading(line: String): Boolean {
		val trimmed = line.trim()
		val lower = trimmed.lowercase()
		val withoutParentheses = parentheticalPattern.replace(lower, " ").trim()
		val isExplicitNote = percentLinePattern.containsMatchIn(lower) ||
			noteLinePrefixes.any { prefix -> lower.startsWith(prefix) } ||
			lineClauses.any { clause -> lower.contains(clause) }
		return !isExplicitNote && (
			headingLinePrefixes.any { prefix -> lower.startsWith(prefix) } ||
				lower in exactLines ||
				withoutParentheses in exactLines ||
				isStructuralHeading(trimmed, lower, includeAsteriskNotes = false)
			)
	}

	fun isSectionHeadingOrNote(line: String): Boolean {
		val lower = line.trim().lowercase()
		val withoutParentheses = parentheticalPattern.replace(lower, " ").trim()
		return percentLinePattern.containsMatchIn(lower) ||
			linePrefixes.any { prefix -> lower.startsWith(prefix) } ||
			lineClauses.any { clause -> lower.contains(clause) } ||
			lower in exactLines ||
			withoutParentheses in exactLines ||
			isStructuralHeading(line.trim(), lower, includeAsteriskNotes = true)
	}

	private fun isStructuralHeading(
		original: String,
		lower: String,
		includeAsteriskNotes: Boolean,
	): Boolean {
		val withoutParentheses = parentheticalPattern.replace(lower, " ")
		val stripped = asteriskClausePattern.replace(withoutParentheses, " ").trim()
		val words = stripped
			.split(whitespacePattern)
			.map { word -> word.trim(',', ':', '.') }
			.filter { word -> word.isNotEmpty() }
		if (words.isEmpty() || stripped.any(Char::isDigit)) {
			return false
		}
		val lastWord = words.last()
		val firstWord = words.first()
		val hasAsteriskNote = includeAsteriskNotes &&
			lower.contains('*') &&
			words.size >= MIN_ASTERISK_HEADING_WORDS
		val isUppercaseHeading = original == original.uppercase() &&
			original.any(Char::isLetter) &&
			words.size >= MIN_UPPERCASE_HEADING_WORDS
		return hasAsteriskNote ||
			isUppercaseHeading ||
			lastWord in headingLastWords ||
			(lastWord in doughHeadingLastWords && firstWord in doughHeadingFirstWords)
	}
}

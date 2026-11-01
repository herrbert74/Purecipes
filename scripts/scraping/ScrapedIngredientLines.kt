import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

const val MAX_INGREDIENT_LENGTH = 255
const val FRACTION_MATCH_TOLERANCE = 0.02
const val WHOLE_NUMBER_ONE = 1
const val SCRAPED_INGREDIENT_RULES_CHECKSUM = "contextual-headings-v28"

data class ScrapedLineOverride(
	val kind: String = "",
	val groupName: String = "",
)

data class ProcessedScrapedIngredient(
	val text: String,
	val requirement: String = "REQUIRED",
	val alternativeGroupKey: Int? = null,
)

private val optionalPrefixRegex = Regex(
	pattern = """^optional[:\s,-]+(.+)$""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val prepLabelPrefixRegex = Regex(
	pattern = """^(?:egg wash|glaze|wash):\s*""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val optionalParentheticalRegex = Regex(
	pattern = """\(\s*optional\s*\)""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val toGarnishOrServeSuffixRegex = Regex(
	pattern = """[,;]?\s*\(?\s*(to garnish|to serve|for garnish|for serving)\s*\)?\s*$""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val plusExtraToServeSuffixRegex = Regex(
	pattern = """\bplus extra\b.*\bto serve\s*$""",
	options = setOf(RegexOption.IGNORE_CASE),
)

fun parseIngredientRequirement(raw: String): ProcessedScrapedIngredient {
	var text = raw.trim().removePrefix("-").removePrefix("*").trim()
	var requirement = "REQUIRED"

	optionalPrefixRegex.matchEntire(text)?.let { match ->
		val stripped = match.groupValues[1].trim()
		if (stripped.isNotBlank()) {
			text = prepLabelPrefixRegex.replace(stripped, "").trim()
			requirement = "OPTIONAL"
		}
	}

	text = prepLabelPrefixRegex.replace(text, "").trim()

	if (requirement == "REQUIRED" && optionalParentheticalRegex.containsMatchIn(text)) {
		text = optionalParentheticalRegex.replace(text, "").trim().trimEnd(',', ';')
		requirement = "OPTIONAL"
	}

	if (
		requirement == "REQUIRED" &&
		(
			toGarnishOrServeSuffixRegex.containsMatchIn(text) ||
				plusExtraToServeSuffixRegex.containsMatchIn(text)
			)
	) {
		requirement = "OPTIONAL"
	}

	return ProcessedScrapedIngredient(text = text, requirement = requirement)
}

private val commonCookingFractionTexts = listOf(
	"1/2",
	"1/3",
	"2/3",
	"1/4",
	"3/4",
	"1/8",
	"3/8",
	"5/8",
	"7/8",
)

val leadingDecimalQuantityRegex = Regex("""^(\d+\.\d+)(\s+)(?<rest>.*)$""")

fun formatQuantityAsFraction(value: Double): String? {
	val whole = floor(value).toInt()
	val remainder = value - whole

	return when {
		remainder < FRACTION_MATCH_TOLERANCE -> whole.toString()
		WHOLE_NUMBER_ONE - remainder < FRACTION_MATCH_TOLERANCE -> (whole + WHOLE_NUMBER_ONE).toString()
		else -> {
			val matchedFraction = commonCookingFractionTexts.firstOrNull { fractionText ->
				val parts = fractionText.split('/')
				val numerator = parts.first().toInt()
				val denominator = parts.last().toInt()
				abs(remainder - numerator.toDouble() / denominator) < FRACTION_MATCH_TOLERANCE
			}
			matchedFraction?.let { fraction ->
				if (whole > 0) {
					"$whole $fraction"
				} else {
					fraction
				}
			}
		}
	}
}

fun restoreFractionalQuantities(ingredient: String): String {
	val match = leadingDecimalQuantityRegex.find(ingredient)
	val value = match?.groupValues?.get(1)?.toDoubleOrNull()
	val formatted = value?.let(::formatQuantityAsFraction)
	return if (match != null && formatted != null) {
		formatted + match.groupValues[2] + match.groups["rest"]!!.value
	} else {
		ingredient
	}
}

val ingredientHeadingPrefixFilters = listOf(
	"for ",
	"pre-dough",
	"special equipment",
	"equipment list",
	"in the box",
	"from your cupboard",
	"shopping list",
	"serve with",
)

val ingredientHeadingExactFilters = setOf(
	"dough",
	"filling",
	"for",
	"garnish",
	"garnishes",
	"marinade",
	"salad",
	"sauce",
	"tag",
	"tags",
	"toppings",
)

private const val INGREDIENT_QUANTITY_PATTERN = """(?:\d+(?:\.\d+)?|\d+\s+\d+/\d+|\d+/\d+)"""

private const val INGREDIENT_UNIT_TOKEN_PATTERN = """[A-Za-z][A-Za-z'-]*"""

private const val QUANTITY_UNIT_BOUNDARY_PATTERN = """(?=[\s,.;]|\z)"""

val ingredientQuantityWithUnitPattern =
	"""$INGREDIENT_QUANTITY_PATTERN$INGREDIENT_UNIT_TOKEN_PATTERN$QUANTITY_UNIT_BOUNDARY_PATTERN"""

val concatenatedIngredientSplitRegex = Regex(
	pattern = """(?<=[\p{L})])(?=$ingredientQuantityWithUnitPattern)""",
	options = setOf(RegexOption.IGNORE_CASE),
)

val quantityWithoutSpaceBeforeUnitRegex = Regex(
	pattern =
		"""(?<![\p{L}\d.])(?<qty>$INGREDIENT_QUANTITY_PATTERN)""" +
			"""(?<unit>$INGREDIENT_UNIT_TOKEN_PATTERN)$QUANTITY_UNIT_BOUNDARY_PATTERN""",
	options = setOf(RegexOption.IGNORE_CASE),
)

private val retailerBrandPhraseRegex = Regex(
	pattern =
		"""(?:,?\s*)?\b(?:by\s+)?(?:sainsbury(?:['’]s|s)?|tesco(?:['’]s)?|waitrose|asda|aldi|lidl|""" +
			"""morrisons?|m\s*&\s*s|marks\s*(?:&\s*|and\s+)?spencer)(?![\p{L}])""",
	options = setOf(RegexOption.IGNORE_CASE),
)

const val MAX_PUNCTUATION_BOUND_UNIT_LENGTH = 4

fun allowsPunctuationAfterUnit(unit: String): Boolean =
	unit.first().isUpperCase() || unit.length <= MAX_PUNCTUATION_BOUND_UNIT_LENGTH

fun addSpaceBetweenQuantityAndUnit(ingredient: String): String =
	quantityWithoutSpaceBeforeUnitRegex.replace(ingredient) { match ->
		val qty = match.groups["qty"]!!.value
		val unit = match.groups["unit"]!!.value
		val boundaryChar = ingredient.getOrNull(match.range.last + 1)
		if (boundaryChar != null && boundaryChar in ",.;" && !allowsPunctuationAfterUnit(unit)) {
			return@replace match.value
		}
		"$qty $unit"
	}

fun stripRetailerBrands(ingredient: String): String {
	val stripped = retailerBrandPhraseRegex.replace(ingredient, " ")
	if (stripped == ingredient) {
		return ingredient
	}
	return stripped
		.replace(Regex("""\s*,\s*,"""), ",")
		.replace(Regex("""\s{2,}"""), " ")
		.replace(Regex("""\s+,"""), ",")
		.trim()
		.trimStart(',', ';', '.')
		.trimEnd(',', ';', '.')
		.replace(Regex("""\s{2,}"""), " ")
		.trim()
}

private val soakedInClauseRegex = Regex(""",\s*(soaked in .*)$""", RegexOption.IGNORE_CASE)

fun normalizeIngredientText(raw: String): String {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank()) {
		return raw
	}
	val withParenthesizedSoak = soakedInClauseRegex.replace(normalizedWhitespace, " ($1)")
	val transformed = stripRetailerBrands(
		restoreFractionalQuantities(addSpaceBetweenQuantityAndUnit(withParenthesizedSoak)),
	)
	return if (transformed.length <= MAX_INGREDIENT_LENGTH) {
		transformed
	} else {
		transformed.take(MAX_INGREDIENT_LENGTH)
	}
}

fun splitConcatenatedIngredient(line: String): List<String> {
	val splitPositions = concatenatedIngredientSplitRegex
		.findAll(line)
		.map { it.range.first }
		.filter { it > 0 }
		.toList()
	if (splitPositions.isEmpty()) {
		return listOf(line)
	}

	val parts = mutableListOf<String>()
	var start = 0
	splitPositions.forEach { position ->
		val part = line.substring(start, position).trim()
		if (part.isNotBlank()) {
			parts += part
		}
		start = position
	}
	val lastPart = line.substring(start).trim()
	if (lastPart.isNotBlank()) {
		parts += lastPart
	}
	return parts.ifEmpty { listOf(line) }
}

fun splitIngredientLine(raw: String): List<String> {
	val normalized = raw.replace("\r\n", "\n").trim()
	if (normalized.isBlank()) {
		return emptyList()
	}

	val lineParts = normalized
		.split('\n')
		.map { line -> line.trim() }
		.filter { line -> line.isNotBlank() }
	return joinOrContinuedLines(lineParts).flatMap { trimmedLine ->
		val shouldSplitConcatenated =
			trimmedLine.length > MAX_INGREDIENT_LENGTH ||
				concatenatedIngredientSplitRegex.findAll(trimmedLine).any { it.range.first > 0 }
		if (shouldSplitConcatenated) {
			splitConcatenatedIngredient(trimmedLine)
		} else {
			listOf(trimmedLine)
		}
	}
}

internal fun joinOrContinuedLines(lines: List<String>): List<String> {
	if (lines.isEmpty()) {
		return emptyList()
	}
	val joined = mutableListOf<String>()
	var index = 0
	while (index < lines.size) {
		var line = lines[index]
		while (
			line.endsWith(" or", ignoreCase = true) &&
			index + 1 < lines.size
		) {
			index++
			line = "${line.trimEnd()} ${lines[index]}"
		}
		joined += line
		index++
	}
	return joined
}

val ingredientToolKeywords = listOf(
	"baking sheet",
	"blender",
	"board",
	"bowl",
	"cutter",
	"colander",
	"food processor",
	"grill pan",
	"instant pot",
	"kitchen paper",
	"knife",
	"mandoline",
	"microplane",
	"pan",
	"pastry bag",
	"pot",
	"pressure cooker",
	"saucepan",
	"sheet",
	"skewer",
	"slotted spoon",
	"spoon",
	"toothpick",
	"whisk",
)

val standaloneEquipmentKeywords = listOf(
	"biscuit cutter",
	"cookie cutter",
	"flan ring",
	"paper straw",
	"paper straws",
	"pastry cutter",
	"pudding ring",
	"pudding rings",
	"skewer",
	"skewers",
	"tart ring",
	"toothpick",
	"toothpicks",
)

val structuralHeadingExactFilters = setOf(
	"aromatic mix",
	"base",
	"batter",
	"bread spice",
	"cake mixture",
	"chocolate, almond and orange biscotti",
	"crumble",
	"crust",
	"custard",
	"enriched bread dough",
	"equipment",
	"equipment list",
	"final dough",
	"first dough",
	"frangipane",
	"frangipane filling",
	"hot water crust pastry",
	"inclusions",
	"lime filling",
	"lime fillng",
	"lye bath",
	"macaron paste",
	"main dough",
	"meringue",
	"oatmeal",
	"paste",
	"pastry",
	"pate",
	"pâté",
	"pizza dough",
	"poolish",
	"porridge",
	"rough puff pastry",
	"rum butter",
	"rye sourdough",
	"scald",
	"second dough",
	"shortcrust pastry",
	"slaw",
	"sourdough",
	"special equipment",
	"spice blend",
	"spice mix",
	"spice paste",
	"sponge cake",
	"stiff levain",
	"sweet pastry",
	"sweet shortcrust",
	"sweet shortcrust pastry",
	"sweet stiff levain",
	"swiss meringue",
	"to assemble",
	"to decorate",
	"to finish",
	"to serve",
	"tomato sauce",
	"topping",
	"toppings",
	"wash",
	"yeast",
)

private val headingLastWords = setOf(
	"additions",
	"alternative",
	"alternatives",
	"bath",
	"boiling",
	"boule",
	"buttercream",
	"conchas",
	"crumble",
	"crust",
	"custard",
	"ferment",
	"filling",
	"flavouring",
	"ingredients",
	"inclusions",
	"levain",
	"loaf",
	"meringue",
	"pastry",
	"percentages",
	"poolish",
	"porridge",
	"solution",
	"sourdough",
	"starter",
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
	"pasta",
	"second",
)

private const val MIN_ASTERISK_HEADING_WORDS = 2
private const val MIN_UPPERCASE_HEADING_WORDS = 2
private const val MAX_DOUGH_HEADING_WORDS = 3

private val excludedHeadingFirstWords = setOf(
	"all",
	"both",
	"cup",
	"cups",
	"dash",
	"each",
	"g",
	"half",
	"kg",
	"l",
	"lbs",
	"ml",
	"oz",
	"part",
	"pinch",
	"rest",
	"store",
	"store-bought",
	"tablespoon",
	"tablespoons",
	"tbsp",
	"tbsps",
	"teaspoon",
	"teaspoons",
	"tsp",
	"tsps",
)

private val percentLineRegex = Regex("""^(?:optional\s+|~\s*|approx(?:\.|\s+))?\d+(?:\.\d+)?\s*%""")

private val notePrefixes = listOf(
	"all of ",
	"all the ",
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
	"~",
)

private val noteClauses = listOf(
	"approximations",
	"volumetric measures",
	"use a scale",
	"kitchen scale",
	"inherently inaccurate",
	"hours time",
	"hour time",
	"ascorbic acid",
	"vitamin c",
)

private val asteriskNoteOnlyPrefixes = listOf(
	"chestnut meal,",
	"chestnut flour and meal,",
	"dried fruit and candied peel alternative",
	"honey solution",
)

private fun isDoughStructuralHeading(
	firstWord: String,
	lastWord: String,
	wordCount: Int,
): Boolean =
	lastWord in doughHeadingLastWords &&
		firstWord in doughHeadingFirstWords &&
		wordCount <= MAX_DOUGH_HEADING_WORDS

private fun isStructuralHeading(
	original: String,
	lower: String,
	includeAsteriskNotes: Boolean = false,
): Boolean {
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ")
	val stripped = withoutParentheses.replace(Regex("""\*.*$"""), " ").trim()
	val words = stripped
		.split(Regex("""\s+"""))
		.map { word -> word.trim(',', ':', '.') }
		.filter { word -> word.isNotEmpty() }
	val firstWord = words.firstOrNull().orEmpty()
	val hasDigitOrFraction = stripped.any { it.isDigit() || it in "½⅓⅔¼¾⅛⅜⅝⅞" }
	if (words.isEmpty() || hasDigitOrFraction || firstWord in excludedHeadingFirstWords) {
		return false
	}
	val lastWord = words.last()
	val hasAsteriskNote = includeAsteriskNotes &&
		lower.contains('*') &&
		words.size >= MIN_ASTERISK_HEADING_WORDS
	val isUppercaseHeading = original == original.uppercase(Locale.ROOT) &&
		original.any(Char::isLetter) &&
		words.size >= MIN_UPPERCASE_HEADING_WORDS
	return hasAsteriskNote ||
		isUppercaseHeading ||
		lastWord in headingLastWords ||
		isDoughStructuralHeading(firstWord, lastWord, words.size)
}

fun isSharedSectionHeading(line: String): Boolean {
	val trimmed = line.trim()
	val lower = trimmed.lowercase(Locale.ROOT)
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ").trim()
	val isExplicitNote = isIgnorableInstructionOrNote(trimmed) ||
		asteriskNoteOnlyPrefixes.any { prefix -> lower.startsWith(prefix) }
	return !isExplicitNote && (
		lower.startsWith("dough for") ||
			lower.startsWith("pre-dough") ||
			lower in structuralHeadingExactFilters ||
			withoutParentheses in structuralHeadingExactFilters ||
			isStructuralHeading(trimmed, lower, includeAsteriskNotes = false)
		)
}

fun isSharedSectionHeadingOrNote(line: String): Boolean {
	val trimmed = line.trim()
	val lower = trimmed.lowercase(Locale.ROOT)
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ").trim()
	return isIgnorableInstructionOrNote(trimmed) ||
		asteriskNoteOnlyPrefixes.any { prefix -> lower.startsWith(prefix) } ||
		lower.startsWith("dough for") ||
		lower.startsWith("pre-dough") ||
		lower in structuralHeadingExactFilters ||
		withoutParentheses in structuralHeadingExactFilters ||
		isStructuralHeading(trimmed, lower, includeAsteriskNotes = true)
}

fun isIgnorableInstructionOrNote(raw: String): Boolean {
	val lower = raw.trim().lowercase(Locale.ROOT)
	return lower.startsWith("%") ||
		percentLineRegex.containsMatchIn(lower) ||
		notePrefixes.any { prefix -> lower.startsWith(prefix) } ||
		noteClauses.any { clause -> lower.contains(clause) }
}

private val trailingNoteRegex = Regex("""\s*[*†‡]\s*(?:see\s+notes?|see\s+below).*$""", RegexOption.IGNORE_CASE)

fun sanitizeIngredientLine(raw: String): ProcessedScrapedIngredient? {
	val withoutTrailingNote = trailingNoteRegex.replace(raw, "").trim()
	val normalizedWhitespace = withoutTrailingNote.removePrefix("-").removePrefix("*").removeSuffix("*").trim()
	if (normalizedWhitespace.isBlank() || isIgnorableInstructionOrNote(normalizedWhitespace)) {
		return null
	}

	val lower = normalizedWhitespace.lowercase(Locale.ROOT)
	val hasDigit = lower.any(Char::isDigit)
	val isHeadingLike =
		isIngredientGroupHeading(raw) ||
			ingredientHeadingExactFilters.contains(lower) ||
			structuralHeadingExactFilters.contains(lower) ||
			lower.contains("recipe follows")
	val isStandaloneEquipment = standaloneEquipmentKeywords.any { keyword ->
		lower.contains(keyword) && !lower.contains("strawb")
	}
	val isEquipmentLike = isStandaloneEquipment ||
		(
			(!hasDigit || lower.contains("set of")) &&
				ingredientToolKeywords.any { keyword -> lower.contains(keyword) }
			)

	return if (isHeadingLike || isEquipmentLike) {
		null
	} else {
		val normalizedText = normalizeIngredientText(normalizedWhitespace)
		parseIngredientRequirement(normalizedText)
	}
}

private val scaledDoughHeadingRegex = Regex(
	"""^(?:dough|batter|crust|pastry|filling|topping)\s+for\s+\d+\s+.*""",
	RegexOption.IGNORE_CASE,
)

private val parenthesizedRecipeFollowsRegex = Regex(
	"""\(\s*recipe\s+follows\s*\)""",
	RegexOption.IGNORE_CASE,
)

fun isIngredientGroupHeading(raw: String): Boolean {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank() || isIgnorableInstructionOrNote(normalizedWhitespace)) {
		return false
	}

	val lower = normalizedWhitespace.lowercase(Locale.ROOT)
	val isOptionalHeading = lower == "optional" || lower == "optional:"
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ").trim()
	val isRecipeFollowsHeading = parenthesizedRecipeFollowsRegex.containsMatchIn(lower) &&
		!startsWithQuantity(withoutParentheses)

	return isOptionalHeading ||
		lower.endsWith(':') ||
		ingredientHeadingPrefixFilters.any { lower.startsWith(it) } ||
		ingredientHeadingExactFilters.contains(lower) ||
		withoutParentheses in structuralHeadingExactFilters ||
		scaledDoughHeadingRegex.matches(withoutParentheses) ||
		isStructuralHeading(normalizedWhitespace, lower) ||
		isRecipeFollowsHeading
}

fun normalizeIngredientGroupName(raw: String): String? {
	val withoutTrailingNote = trailingNoteRegex.replace(raw, "").trim()
	val normalizedWhitespace = withoutTrailingNote.removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank()) {
		return null
	}

	val withoutColon = normalizedWhitespace.removeSuffix(":").trim()
	val withoutPrefix = withoutColon
		.removePrefix("For the ")
		.removePrefix("for the ")
		.removePrefix("For ")
		.removePrefix("for ")
		.trim()

	return withoutPrefix.ifBlank { null }
}

private const val ALTERNATIVE_SEPARATOR = " or "
private const val ALTERNATIVE_SEPARATOR_LENGTH = 4

private const val ALTERNATIVE_LEADING_QUANTITY_PATTERN =
	"""(?:\d+\s+[½⅓⅔¼¾⅛⅜⅝⅞]|\d+[½⅓⅔¼¾⅛⅜⅝⅞]|[½⅓⅔¼¾⅛⅜⅝⅞]|""" +
		"""\d+\s+\d+/\d+|\d+/\d+|\d+(?:\.\d+)?(?:\s*[–-]\s*\d+(?:\.\d+)?)?)"""

private const val ALTERNATIVE_LEADING_UNIT_PATTERN =
	"""(?:g|kg|ml|l|oz|lbs?|tsps?|tbsps?|teaspoons?|tablespoons?|cups?|pinch(?:es)?|dash(?:es)?|""" +
		"""handfuls?|bags?|cans?|tins?|packs?|packages?|packets?|jars?|bottles?|slices?|rashers?|cloves?|""" +
		"""glasses?|wine glasses?|mugs?|""" +
		"""stalks?|sprigs?|heads?|bunch(?:es)?|pieces?|pounds?|ounces?|grams?|kilograms?|""" +
		"""liters?|litres?|milliliters?|millilitres?)"""

private val alternativeQuantityPrefixRegex = Regex(
	pattern = """^($ALTERNATIVE_LEADING_QUANTITY_PATTERN""" +
		"""(?:\s+$ALTERNATIVE_LEADING_UNIT_PATTERN)?(?:\s+of)?(?:\s+|$))""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val alternativeOfPrefixRegex = Regex(
	pattern = """^(.*\bof)\s+""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val trailingParentheticalSuffixRegex = Regex(
	pattern = """\s*\([^)]+\)\s*$""",
)
private val inlineParentheticalRegex = Regex(
	pattern = """\([^)]*\)""",
)

private fun stripSurroundingHyphens(value: String): String =
	value.trim()
		.removePrefix("-")
		.removePrefix("–")
		.removeSuffix("-")
		.removeSuffix("–")
		.trim()

private fun splitAlternativeParts(text: String): List<String> {
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
			val part = stripSurroundingHyphens(text.substring(start, index))
			if (part.isNotBlank()) {
				parts += part
			}
			index += ALTERNATIVE_SEPARATOR_LENGTH
			start = index
			continue
		}
		index++
	}
	val lastPart = stripSurroundingHyphens(text.substring(start))
	if (lastPart.isNotBlank()) {
		parts += lastPart
	}
	return parts.filter { part -> part.isNotBlank() }
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

private fun startsWithQuantity(text: String): Boolean {
	val first = text.firstOrNull() ?: return false
	return first.isDigit() || first in "½⅓⅔¼¾⅛⅜⅝⅞"
}

private fun expandAlternativePart(firstAlternative: String, part: String): String {
	val trimmed = stripSurroundingHyphens(part)
	if (startsWithQuantity(trimmed)) {
		return trimmed
	}

	val quantityPrefix = alternativeQuantityPrefixRegex.find(firstAlternative)?.value
	return when {
		quantityPrefix != null -> quantityPrefix + trimmed
		else -> {
			val ofPrefix = alternativeOfPrefixRegex.find(firstAlternative)?.groupValues?.get(1)
			if (ofPrefix != null) {
				"$ofPrefix $trimmed"
			} else {
				trimmed
			}
		}
	}
}

private val sharedAlternativeNounTokens = setOf(
	"apples",
	"broth",
	"butter",
	"cracker",
	"crackers",
	"flour",
	"milk",
	"oil",
	"salt",
	"stock",
	"sugar",
	"vinegar",
	"water",
)

private fun expandAlternativeParts(parts: List<String>): List<String> {
	val cleanedParts = parts
		.map(::stripSurroundingHyphens)
		.filter { it.isNotBlank() }
	if (cleanedParts.size <= 1) {
		return cleanedParts
	}
	val firstAlternative = cleanedParts.first()
	val expanded = cleanedParts.mapIndexed { partIndex, part ->
		if (partIndex == 0) {
			part.trim()
		} else {
			expandAlternativePart(firstAlternative, part)
		}
	}
	val sharedSuffix = trailingParentheticalSuffixRegex.find(cleanedParts.last())?.value
	val lastPartBase = inlineParentheticalRegex.replace(cleanedParts.last(), " ").trim()
	val firstLower = firstAlternative.lowercase(Locale.ROOT)
	val lastLower = lastPartBase.lowercase(Locale.ROOT)
	val isKosherSaltSuffix = lastLower.contains("kosher salt") && !firstLower.contains("salt")
	val firstTokens = firstLower.split(Regex("""[^a-zA-Z]+""")).toSet()
	val firstHasNoun = firstTokens.any { token ->
		sharedAlternativeNounTokens.any { noun -> token == noun || token.endsWith(noun) }
	}
	val lastNoun = lastLower
		.split(Regex("""[^a-zA-Z]+"""))
		.lastOrNull { it in sharedAlternativeNounTokens }
	val sharedNounSuffix = when {
		isKosherSaltSuffix -> " kosher salt"
		!firstHasNoun && lastNoun != null -> " $lastNoun"
		else -> ""
	}

	return expanded.mapIndexed { partIndex, text ->
		if (partIndex == 0) {
			val parenSuffix = if (sharedSuffix != null && !firstAlternative.contains('(')) sharedSuffix else ""
			text + sharedNounSuffix + parenSuffix
		} else {
			text
		}
	}
}

private val keepSingleAlternativePhrases = listOf(
	" are fine",
	" is fine",
	"a combination",
	"a mixture",
	"any combination",
	"any mixture",
	"ideally ",
	"of your choice",
	"preferably ",
	"available in ",
	"if you like",
	"as needed",
	"to taste",
	"more to taste",
	"such as ",
	"serving suggestion",
	"your choice",
)

private fun shouldKeepAlternativeAsSingle(text: String, parts: List<String>): Boolean {
	val withoutParentheticals = inlineParentheticalRegex.replace(text, " ")
	return text.contains(';') ||
		keepSingleAlternativePhrases.any { phrase ->
			withoutParentheticals.contains(phrase, ignoreCase = true)
		} ||
		parts.any(::isIncompleteAlternativeFoodName)
}

fun expandProcessedAlternatives(sanitized: ProcessedScrapedIngredient): List<ProcessedScrapedIngredient> {
	val parts = splitAlternativeParts(sanitized.text)
	val keepAsSingle = sanitized.requirement == "OPTIONAL" ||
		parts.size <= 1 ||
		shouldKeepAlternativeAsSingle(sanitized.text, parts)
	if (keepAsSingle) {
		return listOf(sanitized)
	}
	val alternativeTexts = expandAlternativeParts(parts)
	return if (alternativeTexts.size <= 1) {
		listOf(sanitized)
	} else {
		alternativeTexts.map { text ->
			ProcessedScrapedIngredient(text = text, requirement = "ALTERNATIVE")
		}
	}
}

private val incompleteAlternativeTokens = setOf(
	"all",
	"any",
	"available",
	"baby",
	"black",
	"bleached",
	"boiling",
	"both",
	"bottled",
	"bought",
	"broken",
	"brown",
	"buttered",
	"caster",
	"chilled",
	"choice",
	"chopped",
	"chunks",
	"clear",
	"coarse",
	"coarsely",
	"cold",
	"combination",
	"cooked",
	"crushed",
	"cubed",
	"cubes",
	"cut",
	"dark",
	"diced",
	"distilled",
	"dried",
	"dry",
	"either",
	"extra",
	"fat",
	"favorite",
	"favourite",
	"filtered",
	"fine",
	"finely",
	"flake",
	"flaked",
	"flakes",
	"flat",
	"fresh",
	"freshly",
	"frozen",
	"golden",
	"granules",
	"grate",
	"grated",
	"green",
	"ground",
	"half",
	"halved",
	"halves",
	"heavy",
	"homemade",
	"hot",
	"icing",
	"inch",
	"into",
	"jumbo",
	"kosher",
	"large",
	"light",
	"like",
	"low",
	"lukewarm",
	"mashed",
	"matchsticks",
	"medium",
	"melted",
	"mild",
	"minced",
	"mineral",
	"mini",
	"mix",
	"mixture",
	"needed",
	"nonfat",
	"online",
	"one",
	"ones",
	"organic",
	"package",
	"peeled",
	"pieces",
	"plain",
	"plum",
	"poached",
	"powder",
	"powders",
	"preferably",
	"puree",
	"pureed",
	"quartered",
	"quarters",
	"raw",
	"red",
	"regular",
	"ripe",
	"roasted",
	"rolled",
	"roma",
	"rough",
	"roughly",
	"rye",
	"salted",
	"serving",
	"sharp",
	"shredded",
	"sifted",
	"similar",
	"slice",
	"sliced",
	"slices",
	"small",
	"smoked",
	"smooth",
	"soft",
	"softened",
	"sparkling",
	"split",
	"still",
	"stoneground",
	"store",
	"strong",
	"substitute",
	"suggestion",
	"suggestions",
	"sweet",
	"taste",
	"thick",
	"thin",
	"thinly",
	"toasted",
	"unbleached",
	"unrefined",
	"unsalted",
	"unsweetened",
	"variety",
	"vegetable",
	"warm",
	"white",
	"whole",
	"wholemeal",
	"yellow",
)

private const val MIN_ALTERNATIVE_FOOD_TOKEN_LENGTH = 3

private fun alternativeFoodTokens(text: String): List<String> {
	val withoutParentheticals = inlineParentheticalRegex.replace(text, " ").trim()
	val withoutQuantity = alternativeQuantityPrefixRegex.replace(withoutParentheticals, "")
	return withoutQuantity
		.lowercase(Locale.ROOT)
		.split(Regex("[^a-z0-9]+"))
		.filter { token ->
			token.length >= MIN_ALTERNATIVE_FOOD_TOKEN_LENGTH && !token.all(Char::isDigit)
		}
}

private fun isIncompleteAlternativeFoodName(text: String): Boolean {
	val tokens = alternativeFoodTokens(text)
	return tokens.isEmpty() || tokens.all { token -> token in incompleteAlternativeTokens }
}

fun appendSanitizedIngredientLines(
	currentItems: MutableList<ProcessedScrapedIngredient>,
	rawItem: String,
	storedRequirement: String = "REQUIRED",
	nextAlternativeGroupKey: () -> Int,
) {
	splitIngredientLine(rawItem).forEach { line ->
		sanitizeIngredientLine(line)?.let { sanitizedItem ->
			val effectiveSanitized = if (storedRequirement == "OPTIONAL" && sanitizedItem.requirement == "REQUIRED") {
				sanitizedItem.copy(requirement = "OPTIONAL")
			} else {
				sanitizedItem
			}
			val expandedItems = expandProcessedAlternatives(effectiveSanitized)
			val alternativeCount = expandedItems.count { item -> item.requirement == "ALTERNATIVE" }
			if (alternativeCount > 1) {
				val groupKey = nextAlternativeGroupKey()
				currentItems.addAll(
					expandedItems.map { item ->
						if (item.requirement == "ALTERNATIVE") {
							item.copy(alternativeGroupKey = groupKey)
						} else {
							item
						}
					},
				)
			} else {
				currentItems.addAll(expandedItems)
			}
		}
	}
}

fun isEquipmentGroupName(name: String?): Boolean {
	if (name.isNullOrBlank()) {
		return false
	}
	val lower = name.trim().lowercase(Locale.ROOT)
	return lower == "equipment" ||
		lower == "equipment list" ||
		lower == "special equipment" ||
		lower.startsWith("equipment ") ||
		lower.endsWith(" equipment")
}

fun isIgnorableGroupName(name: String?): Boolean {
	if (name.isNullOrBlank()) {
		return false
	}
	val lower = name.trim().lowercase(Locale.ROOT).removeSuffix(":")
	return isEquipmentGroupName(lower) || lower == "tag" || lower == "tags"
}

fun processScrapedIngredientGroups(
	groupName: String?,
	rawItems: List<String>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> =
	processStoredScrapedIngredientGroups(
		groupName = groupName,
		storedItems = rawItems.map { rawItem -> ProcessedScrapedIngredient(text = rawItem) },
	)

private val storedClusterQuantityPrefixRegex = Regex(
	pattern = """^((?:\d+\s+[½⅓⅔¼¾⅛⅜⅝⅞]|\d+[½⅓⅔¼¾⅛⅜⅝⅞]|[½⅓⅔¼¾⅛⅜⅝⅞]|""" +
		"""\d+\s+\d+/\d+|\d+/\d+|\d+(?:\.\d+)?(?:\s*[–-]\s*\d+(?:\.\d+)?)?)""" +
		"""(?:\s+[a-zA-Z.]+)?(?:\s+of)?\s+)""",
	options = setOf(RegexOption.IGNORE_CASE),
)

private val storedClusterOfPrefixRegex = Regex(
	pattern = """^(.*\bof\s+)""",
	options = setOf(RegexOption.IGNORE_CASE),
)

fun reconstructJoinedAlternativeClusterText(cluster: List<ProcessedScrapedIngredient>): String {
	if (cluster.isEmpty()) {
		return ""
	}
	val firstRaw = cluster.first().text.trim()
	val lastRaw = cluster.last().text.trim()
	val sharedSuffix = trailingParentheticalSuffixRegex.find(lastRaw)?.value
	val firstText = if (
		cluster.size > 1 &&
		sharedSuffix != null &&
		firstRaw.endsWith(sharedSuffix)
	) {
		firstRaw.removeSuffix(sharedSuffix).trim()
	} else {
		firstRaw
	}
	val sharedPrefix = storedClusterQuantityPrefixRegex.find(firstText)?.value
		?: storedClusterOfPrefixRegex.find(firstText)?.value

	return cluster.mapIndexed { index, row ->
		if (index == 0) {
			firstText
		} else {
			val text = row.text.trim()
			val canStrip = sharedPrefix != null &&
				text.startsWith(sharedPrefix, ignoreCase = true) &&
				!startsWithQuantity(text.substring(sharedPrefix.length).trim())
			if (canStrip) {
				text.substring(sharedPrefix.length).trim()
			} else {
				text
			}
		}
	}.joinToString(separator = " or ")
}

private fun storedAlternativeClusterAt(
	items: List<ProcessedScrapedIngredient>,
	index: Int,
): List<ProcessedScrapedIngredient> {
	val item = items[index]
	val groupKey = item.alternativeGroupKey
	val isStart = item.requirement == "ALTERNATIVE" &&
		groupKey != null &&
		(index == 0 || items[index - 1].alternativeGroupKey != groupKey)
	return if (!isStart) {
		emptyList()
	} else {
		items.drop(index).takeWhile { candidate ->
			candidate.requirement == "ALTERNATIVE" && candidate.alternativeGroupKey == groupKey
		}
	}
}

fun rejoinStoredAlternativeClusters(
	storedItems: List<ProcessedScrapedIngredient>,
): List<ProcessedScrapedIngredient> {
	val result = mutableListOf<ProcessedScrapedIngredient>()
	var index = 0
	while (index < storedItems.size) {
		val cluster = storedAlternativeClusterAt(storedItems, index)
		if (cluster.size < 2) {
			result += storedItems[index]
			index += 1
		} else {
			val joinedText = reconstructJoinedAlternativeClusterText(cluster)
			val expanded = sanitizeIngredientLine(joinedText)?.let(::expandProcessedAlternatives)
			if (expanded != null && expanded.size == 1) {
				result += ProcessedScrapedIngredient(text = joinedText)
			} else {
				result += cluster
			}
			index += cluster.size
		}
	}
	return result
}

data class RecipeHeadingContext(
	val recipeTitle: String = "",
	val recipeInstructions: String = "",
	val lineOverridesByRawText: Map<String, ScrapedLineOverride> = emptyMap(),
)

private data class GroupHeadingEvalInput(
	val rawItem: String,
	val itemIndex: Int,
	val items: List<ProcessedScrapedIngredient>,
	val groupName: String?,
	val hasOtherNamedGroups: Boolean,
	val context: RecipeHeadingContext,
)

private const val MAX_CONTEXTUAL_HEADING_WORDS = 4
private const val MIN_FOLLOWING_QUANTIFIED_LINES = 2
private const val FOLLOWING_QUANTIFIED_WINDOW = 4

private val compositeHeadingExactNames = setOf(
	"aioli",
	"batter",
	"brine",
	"broth",
	"buttercream",
	"chimichurri",
	"chutney",
	"compote",
	"crumble",
	"crust",
	"curd",
	"curry paste",
	"custard",
	"dip",
	"dough",
	"dressing",
	"filling",
	"frosting",
	"ganache",
	"glaze",
	"gravy",
	"hummus",
	"icing",
	"jam",
	"jelly",
	"levain",
	"marinade",
	"mayo",
	"mayonnaise",
	"meringue",
	"mousse",
	"paste",
	"pastry",
	"pesto",
	"pickles",
	"poolish",
	"puree",
	"purée",
	"ragu",
	"ragù",
	"relish",
	"remoulade",
	"rémoulade",
	"roux",
	"rub",
	"salad",
	"salsa",
	"sauce",
	"scald",
	"slaw",
	"starter",
	"stock",
	"streusel",
	"syrup",
	"topping",
	"toppings",
	"vinaigrette",
)

private val compositeHeadingLastWords = setOf(
	"aioli",
	"batter",
	"brine",
	"broth",
	"butter",
	"buttercream",
	"chimichurri",
	"chutney",
	"compote",
	"cream",
	"crumble",
	"crust",
	"curd",
	"custard",
	"dip",
	"dough",
	"dressing",
	"filling",
	"frosting",
	"ganache",
	"glaze",
	"gravy",
	"hummus",
	"icing",
	"levain",
	"marinade",
	"mayo",
	"mayonnaise",
	"meringue",
	"mixture",
	"mousse",
	"paste",
	"pastry",
	"pesto",
	"pickle",
	"pickles",
	"poolish",
	"puree",
	"purée",
	"ragu",
	"ragù",
	"relish",
	"remoulade",
	"rémoulade",
	"roux",
	"rub",
	"salad",
	"salsa",
	"sauce",
	"scald",
	"slaw",
	"spice",
	"spices",
	"starter",
	"stock",
	"streusel",
	"syrup",
	"topping",
	"toppings",
	"vinaigrette",
)

private val nonContextualHeadingFirstWords = setOf(
	"a",
	"an",
	"avocado",
	"baking",
	"boiling",
	"bunch",
	"can",
	"canola",
	"chopped",
	"clove",
	"coarsely",
	"coconut",
	"cold",
	"cooked",
	"cooking",
	"cracked",
	"crumbled",
	"cubed",
	"dash",
	"diced",
	"drizzle",
	"drop",
	"extra",
	"few",
	"filtered",
	"fine",
	"finely",
	"flaky",
	"fresh",
	"freshly",
	"glug",
	"good",
	"grated",
	"ground",
	"groundnut",
	"half",
	"handful",
	"hot",
	"ice",
	"iced",
	"jar",
	"juice",
	"knob",
	"kosher",
	"ladle",
	"large",
	"little",
	"maldon",
	"medium",
	"melted",
	"minced",
	"neutral",
	"nonstick",
	"olive",
	"one",
	"optional",
	"pack",
	"peanut",
	"peel",
	"piece",
	"pinch",
	"prepared",
	"pretzel",
	"quality",
	"quarter",
	"rapeseed",
	"regular",
	"reserved",
	"runny",
	"salted",
	"sea",
	"seasoned",
	"seasoning",
	"sesame",
	"several",
	"shot",
	"shredded",
	"slice",
	"sliced",
	"small",
	"smoked",
	"softened",
	"some",
	"splash",
	"sprig",
	"sprinkle",
	"squeeze",
	"sunflower",
	"thinly",
	"three",
	"thumb",
	"tons",
	"two",
	"unsalted",
	"unsweetened",
	"vegetable",
	"very",
	"warm",
	"zest",
)

private val nonContextualBareIngredientNames = setOf(
	"basil",
	"black pepper",
	"butter",
	"chives",
	"cilantro",
	"coriander",
	"cream",
	"crema",
	"crostini",
	"flour",
	"ice",
	"ketchup",
	"lemon",
	"lettuce",
	"lime",
	"marjoram",
	"mayonnaise",
	"milk",
	"mozarella",
	"mozzarella",
	"mustard",
	"oil",
	"oregano",
	"parsley",
	"pepper",
	"pita",
	"rosemary",
	"roti",
	"salt",
	"sour cream",
	"sugar",
	"thyme",
	"tomato",
	"vodka",
	"water",
	"white pepper",
)

private val parentheticalBackReferenceRegex = Regex(
	pattern = """\(\s*(?:see\b|all\s+of\b|recipe\b|for\s+serving|for\s+garnish|""" +
		"""for\s+dusting|for\s+coating|for\s+the\s+tins|to\s+taste)""",
	option = RegexOption.IGNORE_CASE,
)

private fun isUnquantifiedHeadingCandidate(rawItem: String): Boolean {
	val trimmed = rawItem.trim()
	val hasDisallowedPattern = trimmed.isEmpty() ||
		trimmed.any { ch -> ch.isDigit() || ch in "½⅓⅔¼¾⅛⅜⅝⅞,;" } ||
		parentheticalBackReferenceRegex.containsMatchIn(trimmed) ||
		trimmed.contains(" from above", ignoreCase = true) ||
		trimmed.contains(" to serve", ignoreCase = true) ||
		trimmed.contains(" to decorate", ignoreCase = true) ||
		trimmed.contains(" for sprinkling", ignoreCase = true) ||
		trimmed.contains(" for dusting", ignoreCase = true)
	val cleanLower = inlineParentheticalRegex.replace(trimmed, " ")
		.lowercase(Locale.ROOT)
		.trim()
	val words = cleanLower.split(Regex("""\s+""")).filter(String::isNotEmpty)
	val firstWord = words.firstOrNull()?.trim('.', ':').orEmpty()
	return !hasDisallowedPattern &&
		words.size in 1..MAX_CONTEXTUAL_HEADING_WORDS &&
		firstWord !in nonContextualHeadingFirstWords &&
		cleanLower !in nonContextualBareIngredientNames
}

private fun hasEnoughFollowingQuantifiedLines(
	items: List<ProcessedScrapedIngredient>,
	itemIndex: Int,
): Boolean {
	val nextItem = items.getOrNull(itemIndex + 1)?.text?.trim().orEmpty()
	if (nextItem.isEmpty() || !startsWithQuantity(nextItem)) {
		return false
	}
	val windowEnd = minOf(items.size, itemIndex + 1 + FOLLOWING_QUANTIFIED_WINDOW)
	var quantifiedCount = 0
	for (idx in (itemIndex + 1) until windowEnd) {
		val text = items[idx].text.trim()
		if (isIngredientGroupHeading(text)) {
			break
		}
		if (startsWithQuantity(text)) {
			quantifiedCount += 1
		}
	}
	return quantifiedCount >= MIN_FOLLOWING_QUANTIFIED_LINES
}

private fun isCompositeHeadingName(cleanLower: String): Boolean {
	val lastWord = cleanLower.split(Regex("""\s+""")).lastOrNull().orEmpty()
	return cleanLower in compositeHeadingExactNames || lastWord in compositeHeadingLastWords
}

private fun instructionsPrepareComponent(instructions: String, cleanLower: String): Boolean {
	if (instructions.isBlank() || cleanLower.isBlank()) {
		return false
	}
	val lowerInstructions = instructions.lowercase(Locale.ROOT)
	val phraseTriggers = listOf(
		"for the $cleanLower",
		"make the $cleanLower",
		"making the $cleanLower",
		"to make the $cleanLower",
		"to make $cleanLower",
		"prepare the $cleanLower",
		"preparing the $cleanLower",
		"$cleanLower ingredients",
	)
	return phraseTriggers.any { trigger -> lowerInstructions.contains(trigger) } ||
		instructionsHaveExplicitSectionHeader(instructions, cleanLower)
}

private fun instructionsHaveExplicitSectionHeader(instructions: String, cleanLower: String): Boolean {
	if (instructions.isBlank() || cleanLower.isBlank()) {
		return false
	}
	val lowerInstructions = instructions.lowercase(Locale.ROOT)
	val hasColonSection = lowerInstructions.contains("for the $cleanLower:") ||
		lowerInstructions.contains("for $cleanLower:")
	val hasStandaloneHeaderLine = instructions.lineSequence().any { rawLine ->
		val normalizedLine = inlineParentheticalRegex.replace(rawLine, " ")
			.trim()
			.removeSuffix(":")
			.trim()
			.lowercase(Locale.ROOT)
		normalizedLine == cleanLower ||
			normalizedLine == "for the $cleanLower" ||
			normalizedLine == "making the $cleanLower"
	}
	return hasColonSection || hasStandaloneHeaderLine
}

private fun isContextualGroupHeading(evalInput: GroupHeadingEvalInput): Boolean {
	val isEligible = isUnquantifiedHeadingCandidate(evalInput.rawItem) &&
		hasEnoughFollowingQuantifiedLines(evalInput.items, evalInput.itemIndex)
	if (!isEligible) {
		return false
	}
	val cleanLower = inlineParentheticalRegex.replace(evalInput.rawItem.trim(), " ")
		.lowercase(Locale.ROOT)
		.trim()
	val preparedComposite = isCompositeHeadingName(cleanLower) &&
		instructionsPrepareComponent(evalInput.context.recipeInstructions, cleanLower)
	val explicitInstructionSection =
		instructionsHaveExplicitSectionHeader(evalInput.context.recipeInstructions, cleanLower)
	val matchesMultiGroupTitle = evalInput.itemIndex == 0 &&
		evalInput.groupName.isNullOrBlank() &&
		evalInput.hasOtherNamedGroups &&
		evalInput.context.recipeTitle.trim().lowercase(Locale.ROOT).endsWith(cleanLower)
	return preparedComposite || explicitInstructionSection || matchesMultiGroupTitle
}

private val inlineForTheSplitRegex = Regex("""^(.+?)(For the .+)$""")
private val poolishInlineIngredientsRegex = Regex("""^(Poolish):\s*(\d.*)$""", RegexOption.IGNORE_CASE)
private val scaledDoughGroupNameRegex = Regex("""^a\s+\d+(?:\.\d+)?\s*kg\s+dough\b.*$""", RegexOption.IGNORE_CASE)

private val misclassifiedIngredientGroupNamePrefixes = listOf(
	"a dash of ",
	"a pinch of ",
	"a squeeze of ",
	"good pinch ",
	"l egg",
	"optional: ",
	"salt and pepper to season",
)

private fun appendDemotedItems(
	normalized: MutableList<Pair<String?, MutableList<ProcessedScrapedIngredient>>>,
	demotedItems: List<ProcessedScrapedIngredient>,
) {
	if (normalized.isNotEmpty()) {
		normalized.last().second.addAll(demotedItems)
	} else {
		normalized += (null to demotedItems.toMutableList())
	}
}

private fun demoteRecipeFollowsItems(
	groups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
	trimmedName: String,
	lowerName: String,
	rawItems: List<ProcessedScrapedIngredient>,
): List<ProcessedScrapedIngredient> {
	val hasMatchingSubrecipeGroup = groups.any { (otherName, _) ->
		val other = otherName?.trim()?.lowercase(Locale.ROOT).orEmpty()
		other.isNotEmpty() && other != lowerName && lowerName.contains(other)
	}
	return if (hasMatchingSubrecipeGroup) {
		rawItems
	} else {
		val strippedLine = trimmedName.replace(
			Regex(""",\s*recipe\s+follows\b""", RegexOption.IGNORE_CASE),
			"",
		).trim()
		listOf(ProcessedScrapedIngredient(text = strippedLine)) + rawItems
	}
}

private fun demoteMisclassifiedGroupItems(
	trimmedName: String,
	lowerName: String,
	rawItems: List<ProcessedScrapedIngredient>,
): List<ProcessedScrapedIngredient> {
	val fixedLine = if (lowerName.startsWith("l egg")) {
		"1" + trimmedName.drop(1)
	} else {
		trimmedName
	}
	return listOf(ProcessedScrapedIngredient(text = fixedLine)) + rawItems
}

private fun normalizeStoredRecipeGroups(
	groups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> {
	if (groups.isEmpty()) {
		return emptyList()
	}
	val normalized = mutableListOf<Pair<String?, MutableList<ProcessedScrapedIngredient>>>()
	groups.forEach { (rawGroupName, rawItems) ->
		val trimmedName = rawGroupName?.trim()?.ifBlank { null }
		val lowerName = trimmedName?.lowercase(Locale.ROOT).orEmpty()
		val inlineForTheMatch = trimmedName?.let(inlineForTheSplitRegex::matchEntire)
		val poolishMatch = trimmedName?.let(poolishInlineIngredientsRegex::matchEntire)
		when {
			poolishMatch != null -> {
				val extraItems = splitIngredientLine(poolishMatch.groupValues[2])
					.map { line -> ProcessedScrapedIngredient(text = line) }
				normalized += ("Poolish" to (extraItems + rawItems).toMutableList())
			}

			inlineForTheMatch != null -> {
				val prefixIngredient = ProcessedScrapedIngredient(text = inlineForTheMatch.groupValues[1].trim())
				if (normalized.isNotEmpty()) {
					normalized.last().second += prefixIngredient
				}
				val resolvedHeading = normalizeIngredientGroupName(inlineForTheMatch.groupValues[2])
				normalized += (resolvedHeading to rawItems.toMutableList())
			}

			trimmedName != null && scaledDoughGroupNameRegex.matches(trimmedName) -> {
				normalized += ("Dough" to rawItems.toMutableList())
			}

			trimmedName != null && lowerName.contains(", recipe follows") -> {
				val demoted = demoteRecipeFollowsItems(groups, trimmedName, lowerName, rawItems)
				appendDemotedItems(normalized, demoted)
			}

			trimmedName != null && misclassifiedIngredientGroupNamePrefixes.any { lowerName.startsWith(it) } -> {
				val demoted = demoteMisclassifiedGroupItems(trimmedName, lowerName, rawItems)
				appendDemotedItems(normalized, demoted)
			}

			else -> normalized += (trimmedName to rawItems.toMutableList())
		}
	}
	return normalized.map { (name, items) -> name to items.toList() }
}

fun processRecipeScrapedIngredientGroups(
	groups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
	context: RecipeHeadingContext = RecipeHeadingContext(),
): List<Pair<String?, List<ProcessedScrapedIngredient>>> {
	val repairedGroups = normalizeStoredRecipeGroups(groups)
	val hasOtherNamedGroups = repairedGroups.count { (name, _) -> !name.isNullOrBlank() } >= 1
	return repairedGroups.flatMap { (groupName, storedItems) ->
		processStoredGroupWithContext(
			groupName = groupName,
			storedItems = storedItems,
			hasOtherNamedGroups = hasOtherNamedGroups,
			context = context,
		)
	}
}

fun processStoredScrapedIngredientGroups(
	groupName: String?,
	storedItems: List<ProcessedScrapedIngredient>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> =
	processStoredGroupWithContext(
		groupName = groupName,
		storedItems = storedItems,
		hasOtherNamedGroups = false,
		context = RecipeHeadingContext(),
	)

private fun shouldSkipStoredLine(overrideKind: String, rawItem: String): Boolean =
	overrideKind == "note" ||
		overrideKind == "equipment" ||
		(overrideKind != "ingredient" && isIgnorableInstructionOrNote(rawItem))

private fun isStoredLineHeading(
	overrideKind: String,
	evalInput: GroupHeadingEvalInput,
): Boolean =
	when (overrideKind) {
		"heading" -> true
		"ingredient" -> false
		else -> isIngredientGroupHeading(evalInput.rawItem) || isContextualGroupHeading(evalInput)
	}

private fun appendStoredIngredientItem(
	currentItems: MutableList<ProcessedScrapedIngredient>,
	storedItem: ProcessedScrapedIngredient,
	overrideKind: String,
	nextAlternativeGroupKey: () -> Int,
) {
	val rawItem = storedItem.text
	val keepNormalizedSingle = overrideKind == "ingredient" ||
		(storedItem.requirement == "ALTERNATIVE" && storedItem.alternativeGroupKey != null)
	if (keepNormalizedSingle) {
		currentItems += storedItem.copy(text = normalizeIngredientText(rawItem))
	} else {
		val startIndex = currentItems.size
		appendSanitizedIngredientLines(
			currentItems = currentItems,
			rawItem = rawItem,
			storedRequirement = storedItem.requirement,
			nextAlternativeGroupKey = nextAlternativeGroupKey,
		)
		preserveStoredRequirement(currentItems, startIndex, storedItem)
	}
}

private fun processStoredGroupWithContext(
	groupName: String?,
	storedItems: List<ProcessedScrapedIngredient>,
	hasOtherNamedGroups: Boolean,
	context: RecipeHeadingContext,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> {
	if (storedItems.isEmpty()) {
		return emptyList()
	}

	val rejoinedItems = rejoinStoredAlternativeClusters(storedItems)
	val processedGroups = mutableListOf<Pair<String?, List<ProcessedScrapedIngredient>>>()
	var currentGroupName = groupName
	var currentItems = mutableListOf<ProcessedScrapedIngredient>()
	var nextAlternativeGroupKey = (rejoinedItems.mapNotNull { it.alternativeGroupKey }.maxOrNull() ?: 0) + 1

	rejoinedItems.forEachIndexed { itemIndex, storedItem ->
		val rawItem = storedItem.text
		val override = context.lineOverridesByRawText[rawItem.trim()]
		val overrideKind = override?.kind.orEmpty()
		if (shouldSkipStoredLine(overrideKind, rawItem)) {
			return@forEachIndexed
		}
		val evalInput = GroupHeadingEvalInput(
			rawItem = rawItem,
			itemIndex = itemIndex,
			items = rejoinedItems,
			groupName = currentGroupName,
			hasOtherNamedGroups = hasOtherNamedGroups,
			context = context,
		)
		if (isStoredLineHeading(overrideKind, evalInput)) {
			if (currentItems.isNotEmpty() && !isIgnorableGroupName(currentGroupName)) {
				processedGroups += (currentGroupName to currentItems.toList())
			}
			val overrideGroupName = override?.groupName?.takeIf(String::isNotBlank)
			currentGroupName = overrideGroupName ?: normalizeIngredientGroupName(rawItem) ?: currentGroupName
			currentItems = mutableListOf()
		} else {
			appendStoredIngredientItem(
				currentItems = currentItems,
				storedItem = storedItem,
				overrideKind = overrideKind,
			) { nextAlternativeGroupKey++ }
		}
	}

	if (currentItems.isNotEmpty() && !isIgnorableGroupName(currentGroupName)) {
		processedGroups += (currentGroupName to currentItems.toList())
	}

	return processedGroups
}

private fun preserveStoredRequirement(
	currentItems: MutableList<ProcessedScrapedIngredient>,
	startIndex: Int,
	storedItem: ProcessedScrapedIngredient,
) {
	if (storedItem.requirement == "REQUIRED") {
		return
	}
	for (i in startIndex until currentItems.size) {
		val added = currentItems[i]
		if (added.requirement == "REQUIRED") {
			currentItems[i] = added.copy(
				requirement = storedItem.requirement,
				alternativeGroupKey = storedItem.alternativeGroupKey,
			)
		}
	}
}

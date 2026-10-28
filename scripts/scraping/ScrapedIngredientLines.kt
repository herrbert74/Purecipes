import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

const val MAX_INGREDIENT_LENGTH = 255
const val FRACTION_MATCH_TOLERANCE = 0.02
const val WHOLE_NUMBER_ONE = 1
const val SCRAPED_INGREDIENT_RULES_CHECKSUM = "alt-or-incomplete-v20"

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
	"special equipment",
	"equipment list",
	"in the box",
	"from your cupboard",
	"shopping list",
	"serve with",
)

val ingredientHeadingExactFilters = setOf(
	"for",
	"dough",
	"filling",
	"garnish",
	"garnishes",
	"marinade",
	"sauce",
	"salad",
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

val structuralHeadingExactFilters = setOf(
	"aromatic mix",
	"batter",
	"crumble",
	"crust",
	"custard",
	"first dough",
	"second dough",
	"main dough",
	"final dough",
	"inclusions",
	"lye bath",
	"oatmeal",
	"poolish",
	"porridge",
	"scald",
	"sourdough",
	"stiff levain",
	"sweet stiff levain",
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
	"bread",
	"conchas",
	"crumble",
	"crust",
	"custard",
	"ferment",
	"filling",
	"ingredients",
	"inclusions",
	"loaf",
	"percentages",
	"poolish",
	"porridge",
	"solution",
	"sourdough",
	"spice",
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
	"second",
)

private const val MIN_UPPERCASE_HEADING_WORDS = 2
private const val MAX_DOUGH_HEADING_WORDS = 3

private val excludedHeadingFirstWords = setOf("all", "half", "part", "rest", "each", "both", "pinch", "dash")

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
	"volumetric measures",
	"use a scale",
	"kitchen scale",
	"inherently inaccurate",
	"hours time",
	"hour time",
	"ascorbic acid",
	"vitamin c",
)

private fun isStructuralHeading(original: String, lower: String): Boolean {
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ")
	val stripped = withoutParentheses.replace(Regex("""\*.*$"""), " ").trim()
	val words = stripped
		.split(Regex("""\s+"""))
		.map { word -> word.trim(',', ':', '.') }
		.filter { word -> word.isNotEmpty() }
	val firstWord = words.firstOrNull() ?: ""
	if (words.isEmpty() || stripped.any(Char::isDigit) || firstWord in excludedHeadingFirstWords) {
		return false
	}
	val lastWord = words.last()
	val isUppercaseHeading = original == original.uppercase(Locale.ROOT) &&
		original.any(Char::isLetter) &&
		words.size >= MIN_UPPERCASE_HEADING_WORDS
	return isUppercaseHeading ||
		lastWord in headingLastWords ||
		(
			lastWord in doughHeadingLastWords &&
				firstWord in doughHeadingFirstWords &&
				words.size <= MAX_DOUGH_HEADING_WORDS
			)
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
	val isEquipmentLike = !hasDigit && ingredientToolKeywords.any { keyword -> lower.contains(keyword) }

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

fun isIngredientGroupHeading(raw: String): Boolean {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank() || isIgnorableInstructionOrNote(normalizedWhitespace)) {
		return false
	}

	val lower = normalizedWhitespace.lowercase(Locale.ROOT)
	val afterOptional = lower.removePrefix("optional:").trim()
	val isOptionalHeading = lower == "optional" ||
		(lower.startsWith("optional:") && !afterOptional.any(Char::isDigit))
	val withoutParentheses = lower.replace(Regex("""\([^)]*\)"""), " ").trim()

	return isOptionalHeading ||
		lower.endsWith(':') ||
		ingredientHeadingPrefixFilters.any { lower.startsWith(it) } ||
		ingredientHeadingExactFilters.contains(lower) ||
		withoutParentheses in structuralHeadingExactFilters ||
		scaledDoughHeadingRegex.matches(withoutParentheses) ||
		isStructuralHeading(normalizedWhitespace, lower) ||
		lower.contains("recipe follows")
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
	"""(?:\d+\s+\d+/\d+|\d+/\d+|\d+(?:\.\d+)?(?:\s*[–-]\s*\d+(?:\.\d+)?)?)"""

private const val ALTERNATIVE_LEADING_UNIT_PATTERN =
	"""(?:g|kg|ml|l|oz|lbs?|tsps?|tbsps?|teaspoons?|tablespoons?|cups?|pinch(?:es)?|dash(?:es)?|""" +
		"""handfuls?|bags?|cans?|tins?|packs?|packages?|packets?|jars?|bottles?|slices?|rashers?|cloves?|""" +
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

private fun expandAlternativePart(firstAlternative: String, part: String): String {
	val trimmed = stripSurroundingHyphens(part)
	if (trimmed.firstOrNull()?.isDigit() == true) {
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
	"broth",
	"butter",
	"cracker",
	"crackers",
	"flour",
	"milk",
	"oil",
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
	val lastNoun = lastPartBase.split(Regex("""\s+""")).lastOrNull()?.lowercase(Locale.ROOT)
	val needsSharedNoun = lastNoun != null &&
		lastNoun in sharedAlternativeNounTokens &&
		!firstAlternative.contains(lastNoun, ignoreCase = true)
	val sharedNounSuffix = if (needsSharedNoun) " $lastNoun" else ""

	return expanded.mapIndexed { partIndex, text ->
		if (partIndex == 0) {
			val parenSuffix = if (sharedSuffix != null && !firstAlternative.contains('(')) sharedSuffix else ""
			text + sharedNounSuffix + parenSuffix
		} else {
			text
		}
	}
}

fun expandProcessedAlternatives(sanitized: ProcessedScrapedIngredient): List<ProcessedScrapedIngredient> {
	val parts = splitAlternativeParts(sanitized.text)
	val keepAsSingle = sanitized.requirement == "OPTIONAL" ||
		parts.size <= 1 ||
		sanitized.text.contains(';') ||
		sanitized.text.contains(" are fine", ignoreCase = true) ||
		sanitized.text.contains(" is fine", ignoreCase = true) ||
		sanitized.text.contains("ideally ", ignoreCase = true) ||
		sanitized.text.contains("preferably ", ignoreCase = true) ||
		sanitized.text.contains("available in ", ignoreCase = true) ||
		sanitized.text.contains("if you like", ignoreCase = true) ||
		parts.any(::isIncompleteAlternativeFoodName)
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
	"available",
	"baby",
	"black",
	"bleached",
	"boiling",
	"bottled",
	"brown",
	"chilled",
	"chopped",
	"chunks",
	"clear",
	"coarse",
	"coarsely",
	"cold",
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
	"extra",
	"filtered",
	"fine",
	"finely",
	"flake",
	"flaked",
	"flakes",
	"flat",
	"fresh",
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
	"hot",
	"jumbo",
	"large",
	"light",
	"like",
	"lukewarm",
	"mashed",
	"matchsticks",
	"medium",
	"melted",
	"mild",
	"minced",
	"mineral",
	"mini",
	"online",
	"organic",
	"peeled",
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
	"sharp",
	"shredded",
	"sifted",
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
	"strong",
	"sweet",
	"thick",
	"thin",
	"thinly",
	"toasted",
	"unbleached",
	"unrefined",
	"unsalted",
	"unsweetened",
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
	nextAlternativeGroupKey: () -> Int,
) {
	splitIngredientLine(rawItem).forEach { line ->
		sanitizeIngredientLine(line)?.let { sanitizedItem ->
			val expandedItems = expandProcessedAlternatives(sanitizedItem)
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

fun processScrapedIngredientGroups(
	groupName: String?,
	rawItems: List<String>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> =
	processStoredScrapedIngredientGroups(
		groupName = groupName,
		storedItems = rawItems.map { rawItem -> ProcessedScrapedIngredient(text = rawItem) },
	)

fun processStoredScrapedIngredientGroups(
	groupName: String?,
	storedItems: List<ProcessedScrapedIngredient>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> {
	if (storedItems.isEmpty()) {
		return emptyList()
	}

	val processedGroups = mutableListOf<Pair<String?, List<ProcessedScrapedIngredient>>>()
	var currentGroupName = groupName
	var currentItems = mutableListOf<ProcessedScrapedIngredient>()
	var nextAlternativeGroupKey = (storedItems.mapNotNull { it.alternativeGroupKey }.maxOrNull() ?: 0) + 1

	storedItems.forEach { storedItem ->
		val rawItem = storedItem.text
		if (isIgnorableInstructionOrNote(rawItem)) {
			return@forEach
		}
		if (isIngredientGroupHeading(rawItem)) {
			if (currentItems.isNotEmpty()) {
				processedGroups += (currentGroupName to currentItems.toList())
			}
			currentGroupName = normalizeIngredientGroupName(rawItem) ?: currentGroupName
			currentItems = mutableListOf()
		} else {
			val startIndex = currentItems.size
			appendSanitizedIngredientLines(currentItems, rawItem) { nextAlternativeGroupKey++ }
			preserveStoredRequirement(currentItems, startIndex, storedItem)
		}
	}

	if (currentItems.isNotEmpty()) {
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

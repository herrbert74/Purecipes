import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

const val MAX_INGREDIENT_LENGTH = 255
const val FRACTION_MATCH_TOLERANCE = 0.02
const val WHOLE_NUMBER_ONE = 1
const val SCRAPED_INGREDIENT_RULES_CHECKSUM = "generic-units-v5"

data class ProcessedScrapedIngredient(
	val text: String,
	val requirement: String = "REQUIRED",
)

private val optionalPrefixRegex = Regex(
	pattern = """^optional[:\s,-]+(.+)$""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val optionalParentheticalRegex = Regex(
	pattern = """\(\s*optional\s*\)""",
	options = setOf(RegexOption.IGNORE_CASE),
)
private val toGarnishOrServeSuffixRegex = Regex(
	pattern = """[,;]?\s*(to garnish|to serve|for garnish|for serving)\s*$""",
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
			text = stripped
			requirement = "OPTIONAL"
		}
	}

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

	if (remainder < FRACTION_MATCH_TOLERANCE) {
		return whole.toString()
	}
	if (WHOLE_NUMBER_ONE - remainder < FRACTION_MATCH_TOLERANCE) {
		return (whole + WHOLE_NUMBER_ONE).toString()
	}

	val matchedFraction = commonCookingFractionTexts.firstOrNull { fractionText ->
		val parts = fractionText.split('/')
		val numerator = parts.first().toInt()
		val denominator = parts.last().toInt()
		abs(remainder - numerator.toDouble() / denominator) < FRACTION_MATCH_TOLERANCE
	}

	return matchedFraction?.let { fraction ->
		if (whole > 0) {
			"$whole $fraction"
		} else {
			fraction
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

val ingredientQuantityPattern = """(?:\d+(?:\.\d+)?|\d+\s+\d+/\d+|\d+/\d+)"""

val ingredientUnitTokenPattern = """[A-Za-z][A-Za-z'-]*"""

val quantityUnitBoundaryPattern = """(?=[\s,.;]|\z)"""

val ingredientQuantityWithUnitPattern =
	"""$ingredientQuantityPattern$ingredientUnitTokenPattern$quantityUnitBoundaryPattern"""

val concatenatedIngredientSplitRegex = Regex(
	pattern = """(?<=[\p{L})])(?=$ingredientQuantityWithUnitPattern)""",
	options = setOf(RegexOption.IGNORE_CASE),
)

val quantityWithoutSpaceBeforeUnitRegex = Regex(
	pattern =
		"""(?<![\p{L}\d.])(?<qty>$ingredientQuantityPattern)""" +
			"""(?<unit>$ingredientUnitTokenPattern)$quantityUnitBoundaryPattern""",
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

fun normalizeIngredientText(raw: String): String {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank()) {
		return raw
	}
	val transformed = restoreFractionalQuantities(addSpaceBetweenQuantityAndUnit(normalizedWhitespace))
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

	return normalized
		.split('\n')
		.flatMap { line ->
			val trimmedLine = line.trim()
			if (trimmedLine.isBlank()) {
				emptyList()
			} else {
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

fun sanitizeIngredientLine(raw: String): ProcessedScrapedIngredient? {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank()) {
		return null
	}

	val lower = normalizedWhitespace.lowercase(Locale.ROOT)
	val hasDigit = lower.any(Char::isDigit)
	val isHeadingLike =
		isIngredientGroupHeading(raw) ||
			ingredientHeadingExactFilters.contains(lower) ||
			lower.contains("recipe follows")
	val isEquipmentLike = !hasDigit && ingredientToolKeywords.any { keyword -> lower.contains(keyword) }

	return if (isHeadingLike || isEquipmentLike) {
		null
	} else {
		val normalizedText = normalizeIngredientText(normalizedWhitespace)
		parseIngredientRequirement(normalizedText)
	}
}

fun isIngredientGroupHeading(raw: String): Boolean {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
	if (normalizedWhitespace.isBlank()) {
		return false
	}

	val lower = normalizedWhitespace.lowercase(Locale.ROOT)
	return lower == "optional" ||
		lower.startsWith("optional:") ||
		lower.endsWith(':') ||
		ingredientHeadingPrefixFilters.any { lower.startsWith(it) } ||
		ingredientHeadingExactFilters.contains(lower) ||
		lower.contains("recipe follows")
}

fun normalizeIngredientGroupName(raw: String): String? {
	val normalizedWhitespace = raw.trim().removePrefix("-").removePrefix("*").trim()
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

fun appendSanitizedIngredientLines(
	currentItems: MutableList<ProcessedScrapedIngredient>,
	rawItem: String,
) {
	splitIngredientLine(rawItem).forEach { line ->
		sanitizeIngredientLine(line)?.let { sanitizedItem ->
			currentItems += sanitizedItem
		}
	}
}

fun processScrapedIngredientGroups(
	groupName: String?,
	rawItems: List<String>,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> {
	if (rawItems.isEmpty()) {
		return emptyList()
	}

	val processedGroups = mutableListOf<Pair<String?, List<ProcessedScrapedIngredient>>>()
	var currentGroupName = groupName
	var currentItems = mutableListOf<ProcessedScrapedIngredient>()

	rawItems.forEach { rawItem ->
		if (isIngredientGroupHeading(rawItem)) {
			if (currentItems.isNotEmpty()) {
				processedGroups += (currentGroupName to currentItems.toList())
			}
			currentGroupName = normalizeIngredientGroupName(rawItem) ?: currentGroupName
			currentItems = mutableListOf()
		} else {
			appendSanitizedIngredientLines(currentItems, rawItem)
		}
	}

	if (currentItems.isNotEmpty()) {
		processedGroups += (currentGroupName to currentItems.toList())
	}

	return processedGroups
}

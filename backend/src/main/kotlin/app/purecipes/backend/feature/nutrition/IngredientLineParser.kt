package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.math.RoundingMode

internal object IngredientLineParser {

	private const val QUANTITY_PATTERN = """\d+\s+\d+\s*/\s*\d+|\d+\s*/\s*\d+|\d+(?:\.\d+)?|\.\d+"""
	private const val QUANTITY_SCALE = 4

	private val unicodeFractions = mapOf(
		'¼' to "1/4",
		'½' to "1/2",
		'¾' to "3/4",
		'⅓' to "1/3",
		'⅔' to "2/3",
		'⅕' to "1/5",
		'⅖' to "2/5",
		'⅗' to "3/5",
		'⅘' to "4/5",
		'⅙' to "1/6",
		'⅚' to "5/6",
		'⅛' to "1/8",
		'⅜' to "3/8",
		'⅝' to "5/8",
		'⅞' to "7/8",
	)

	private val leadingQuantityPattern = Regex(
		"""^($QUANTITY_PATTERN)(?:\s*-\s*(?:$QUANTITY_PATTERN))?""",
	)

	private val leadingParentheticalAmountPattern = Regex(
		"""^\(($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)\s*\)""",
		RegexOption.IGNORE_CASE,
	)

	private val leadingParentheticalPattern = Regex("""^\([^)]*\)\s*""")

	private val parentheticalPattern = Regex("""\(([^)]+)\)""")

	private val innerAmountPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val mixedNumberPattern = Regex("""^(\d+)\s+(\d+)\s*/\s*(\d+)$""")

	private val simpleFractionPattern = Regex("""^(\d+)\s*/\s*(\d+)$""")

	private val extraWhitespacePattern = Regex("""\s+""")

	private val packTimesPattern = Regex(
		"""^[x×*]\s*($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)""",
		RegexOption.IGNORE_CASE,
	)

	private val implicitPackPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)""",
		RegexOption.IGNORE_CASE,
	)

	private val sizeTokens = setOf(
		"large",
		"small",
		"medium",
		"extra",
		"extra-large",
		"jumbo",
		"heaping",
		"packed",
		"scant",
		"rounded",
		"level",
	)

	private val knownUnits = setOf(
		"g",
		"kg",
		"ml",
		"l",
		"tsp",
		"tbsp",
		"cup",
		"oz",
		"lb",
		"egg",
		"clove",
		"piece",
	)

	private val consumedUnits = setOf(
		"g",
		"kg",
		"ml",
		"l",
		"tsp",
		"tbsp",
		"cup",
		"oz",
		"lb",
		"clove",
	)

	private val packUnits = setOf(
		"g",
		"kg",
		"ml",
		"l",
		"tsp",
		"tbsp",
		"cup",
		"oz",
		"lb",
	)

	private val containerTokens = setOf(
		"can",
		"cans",
		"jar",
		"jars",
		"tin",
		"tins",
	)

	private val defaultSingleCountUnits = setOf("clove", "piece")

	private val defaultOnePieceTokens = setOf(
		"aubergine",
		"aubergines",
		"avocado",
		"avocados",
		"broccoli",
		"broccolis",
		"cabbage",
		"cabbages",
		"carrot",
		"carrots",
		"cauliflower",
		"cauliflowers",
		"celery",
		"courgette",
		"courgettes",
		"cucumber",
		"cucumbers",
		"eggplant",
		"eggplants",
		"leek",
		"leeks",
		"lemon",
		"lemons",
		"lime",
		"limes",
		"onion",
		"onions",
		"orange",
		"oranges",
		"potato",
		"potatoes",
		"shallot",
		"shallots",
		"tomato",
		"tomatoes",
		"zucchini",
		"zucchinis",
	)

	fun parse(rawLine: String): ParsedIngredientLine {
		val rawText = rawLine.trim()
		if (IngredientVocabulary.isIgnorableIngredientLine(rawText)) {
			return ParsedIngredientLine(
				rawText = rawText,
				quantity = null,
				unit = null,
				parsedName = rawText,
				isMeasurable = false,
			)
		}

		var rest = canonicalizeLine(rawText)
		val leadingAmount = consumeLeadingQuantityAndPack(rest)
		var quantity = leadingAmount.quantity
		var unit = leadingAmount.unit
		rest = leadingAmount.rest

		rest = skipSizeTokens(rest)
		if (unit == null) {
			val consumed = consumeUnit(rest)
			if (consumed != null) {
				unit = consumed.first
				rest = consumed.second
			}
		}
		rest = stripLeadingParenthetical(rest)
		rest = stripContainerWords(rest)

		if (unit == null) {
			val parentheticalMeasure = findParentheticalMeasure(rest)
			if (parentheticalMeasure != null) {
				quantity = parentheticalMeasure.quantity
				unit = parentheticalMeasure.unit
				rest = parentheticalMeasure.parsedName
			}
		}

		if (unit == null) {
			unit = inferCountUnit(rest)
		}
		if (quantity == null && unit in defaultSingleCountUnits) {
			quantity = BigDecimal.ONE
		}

		val parsedName = parsedNameForUnit(unit = unit, rest = rest, rawText = rawText)
		val isMeasurable = quantity != null && unit != null && unit in knownUnits
		return ParsedIngredientLine(
			rawText = rawText,
			quantity = quantity,
			unit = unit,
			parsedName = parsedName,
			isMeasurable = isMeasurable,
		)
	}

	private fun canonicalizeLine(value: String): String {
		val replaced = buildString {
			value.forEach { character ->
				val fraction = unicodeFractions[character]
				if (fraction == null) {
					append(character)
				} else {
					append(' ')
					append(fraction)
					append(' ')
				}
			}
		}
		return extraWhitespacePattern.replace(replaced, " ").trim()
	}

	private fun consumeLeadingQuantityAndPack(value: String): LeadingAmount {
		val leadingParenthetical = leadingParentheticalAmountPattern.find(value)
		if (leadingParenthetical != null) {
			return LeadingAmount(
				quantity = parseQuantity(leadingParenthetical.groupValues[1]),
				unit = normalizeUnit(leadingParenthetical.groupValues[2]),
				rest = value.substring(leadingParenthetical.range.last + 1).trim(),
			)
		}
		val leadingQuantity = leadingQuantityPattern.find(value)
		val quantity = leadingQuantity?.let { match -> parseQuantity(match.groupValues[1]) }
		val afterQuantity = if (leadingQuantity == null) {
			value
		} else {
			value.substring(leadingQuantity.range.last + 1).trim()
		}
		val pack = consumePackQuantity(
			value = afterQuantity,
			allowImplicit = quantity != null,
		)
		return if (pack == null) {
			LeadingAmount(
				quantity = quantity,
				unit = null,
				rest = afterQuantity,
			)
		} else {
			val packCount = quantity ?: BigDecimal.ONE
			LeadingAmount(
				quantity = packCount.multiply(pack.quantity),
				unit = pack.unit,
				rest = pack.rest,
			)
		}
	}

	private fun consumePackQuantity(value: String, allowImplicit: Boolean): PackQuantity? {
		val match = packTimesPattern.find(value)
			?: if (allowImplicit) implicitPackPattern.find(value) else null
		if (match == null) {
			return null
		}
		val quantity = parseQuantity(match.groupValues[1])
		val unit = normalizeUnit(match.groupValues[2])
		return if (quantity != null && unit != null && unit in packUnits) {
			PackQuantity(
				quantity = quantity,
				unit = unit,
				rest = value.substring(match.range.last + 1).trim(),
			)
		} else {
			null
		}
	}

	private fun skipSizeTokens(value: String): String {
		var rest = value.trim()
		while (rest.isNotEmpty()) {
			val token = firstToken(rest).lowercase()
			if (token !in sizeTokens) {
				return rest
			}
			rest = dropFirstWord(rest)
		}
		return rest
	}

	private fun consumeUnit(value: String): Pair<String, String>? {
		val token = firstToken(value)
		val unit = normalizeUnit(token)
		return if (token.isNotEmpty() && unit != null && unit in consumedUnits) {
			unit to dropFirstWord(value)
		} else {
			null
		}
	}

	private fun stripLeadingParenthetical(value: String): String =
		leadingParentheticalPattern.replaceFirst(value, "").trim()

	private fun stripContainerWords(value: String): String {
		val token = firstToken(value).lowercase()
		if (token !in containerTokens) {
			return value.trim()
		}
		var rest = dropFirstWord(value)
		if (firstToken(rest).lowercase() == "of") {
			rest = dropFirstWord(rest)
		}
		return rest
	}

	private fun findParentheticalMeasure(value: String): ParsedIngredientLine? {
		parentheticalPattern.findAll(value).forEach { match ->
			val inner = canonicalizeLine(match.groupValues[1])
			val innerMatch = innerAmountPattern.find(inner) ?: return@forEach
			val quantity = parseQuantity(innerMatch.groupValues[1]) ?: return@forEach
			val unit = normalizeUnit(innerMatch.groupValues[2]) ?: return@forEach
			if (unit !in consumedUnits && unit !in knownUnits) {
				return@forEach
			}
			val parsedName = value.replaceRange(match.range, " ").replace(extraWhitespacePattern, " ").trim()
			return ParsedIngredientLine(
				rawText = value,
				quantity = quantity,
				unit = unit,
				parsedName = parsedName.ifBlank { value },
				isMeasurable = true,
			)
		}
		return null
	}

	private fun inferCountUnit(parsedName: String): String? {
		val first = normalizeUnit(firstToken(parsedName))
		val lookupTokens = NutritionNameNormalizer.forLookup(parsedName)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		val isJuiceOrZest = lookupTokens.any { token -> token == "juice" || token == "zest" }
		val isGarlicClove = lookupTokens.any { token -> token == "garlic" } &&
			lookupTokens.any { token -> token.startsWith("clove") }
		val isBellPepper = lookupTokens.any { token -> token == "bell" } &&
			lookupTokens.any { token -> token == "pepper" }
		val isCornOnTheCob = lookupTokens.any { token -> token == "corn" } &&
			lookupTokens.any { token -> token == "cob" }
		val isProduce = !isJuiceOrZest &&
			(lookupTokens.any { token -> token in defaultOnePieceTokens } || isBellPepper || isCornOnTheCob)
		return when {
			first == "egg" || first == "clove" || first == "piece" -> first
			isGarlicClove -> "clove"
			isProduce -> "piece"
			else -> null
		}
	}

	private fun parsedNameForUnit(unit: String?, rest: String, rawText: String): String {
		if (unit != "clove") {
			return rest.ifBlank { rawText }
		}
		val trimmed = rest.trim()
		return when {
			trimmed.isEmpty() -> "garlic"
			trimmed.equals("ground", ignoreCase = true) -> "ground clove"
			else -> trimmed
		}
	}

	private fun firstToken(value: String): String =
		value.trim().substringBefore(' ').trimEnd(',', ';', '.')

	private fun dropFirstWord(value: String): String {
		val trimmed = value.trim()
		val index = trimmed.indexOfFirst { character -> character.isWhitespace() }
		return if (index < 0) {
			""
		} else {
			trimmed.substring(index).trim()
		}
	}

	private fun parseQuantity(rawQuantity: String): BigDecimal? {
		val trimmed = rawQuantity.trim()
		val mixedNumber = mixedNumberPattern.matchEntire(trimmed)
		val simpleFraction = simpleFractionPattern.matchEntire(trimmed)
		val whole = mixedNumber?.groupValues?.get(1)?.toBigDecimalOrNull()
		val numerator = (mixedNumber?.groupValues?.get(2) ?: simpleFraction?.groupValues?.get(1))
			?.toBigDecimalOrNull()
		val denominator = (mixedNumber?.groupValues?.get(3) ?: simpleFraction?.groupValues?.get(2))
			?.toBigDecimalOrNull()
		val fraction = if (numerator != null && denominator != null && denominator.compareTo(BigDecimal.ZERO) != 0) {
			numerator.divide(denominator, QUANTITY_SCALE, RoundingMode.HALF_UP)
		} else {
			null
		}
		return when {
			trimmed.isEmpty() -> null
			mixedNumber != null && whole != null && fraction != null -> whole.add(fraction)
			simpleFraction != null -> fraction
			else -> trimmed.toBigDecimalOrNull()
		}
	}

	private fun normalizeUnit(rawUnit: String?): String? {
		val unit = rawUnit?.trim()?.lowercase()?.trimEnd('.') ?: return null
		return when (unit) {
			"g", "gram", "grams" -> "g"
			"kg", "kilogram", "kilograms" -> "kg"
			"ml", "milliliter", "millilitre", "milliliters", "millilitres" -> "ml"
			"l", "liter", "litre", "liters", "litres" -> "l"
			"tsp", "tsps", "teaspoon", "teaspoons" -> "tsp"
			"tbsp", "tbsps", "tablespoon", "tablespoons" -> "tbsp"
			"cup", "cups" -> "cup"
			"oz", "ounce", "ounces" -> "oz"
			"lb", "lbs", "pound", "pounds" -> "lb"
			"egg", "eggs" -> "egg"
			"clove", "cloves" -> "clove"
			"piece", "pieces" -> "piece"
			else -> unit
		}
	}

	private data class LeadingAmount(
		val quantity: BigDecimal?,
		val unit: String?,
		val rest: String,
	)

	private data class PackQuantity(
		val quantity: BigDecimal,
		val unit: String,
		val rest: String,
	)
}

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

	private val hyphenatedPackPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-\s*(ounces?|oz|pounds?|lbs?)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val spacedPackPattern = Regex(
		"""^($QUANTITY_PATTERN)\s+(ounces?|oz|pounds?|lbs?)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val aboutAmountPattern = Regex(
		"""^(?:about|approximately|approx\.?)\s+($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val trailingAboutAmountPattern = Regex(
		"""(?:,\s*)?(?:about|approximately|approx\.?)\s+($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val wordQuantities = mapOf(
		"a" to BigDecimal.ONE,
		"an" to BigDecimal.ONE,
		"one" to BigDecimal.ONE,
		"two" to BigDecimal("2"),
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
		"pinch",
		"dash",
		"handful",
	)

	private val pinchUnits = setOf("pinch", "dash")

	private val handfulUnits = setOf("handful")

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

	private val countNounTokens = setOf(
		"stalk",
		"stalks",
		"sprig",
		"sprigs",
		"leaf",
		"leaves",
		"knob",
		"knobs",
		"bulb",
		"bulbs",
		"rasher",
		"rashers",
		"stick",
		"sticks",
		"piece",
		"pieces",
		"bunch",
		"bunches",
		"cube",
		"cubes",
		"fillet",
		"fillets",
		"slice",
		"slices",
	)

	private val teaspoonCountNounTokens = setOf(
		"sprig",
		"sprigs",
		"leaf",
		"leaves",
	)

	private val tablespoonCountNounTokens = setOf(
		"bunch",
		"bunches",
		"handful",
		"handfuls",
	)

	private val countableMeatTokens = setOf(
		"breast",
		"breasts",
		"thigh",
		"thighs",
	)

	private val countableSausageTokens = setOf(
		"sausage",
		"sausages",
	)

	private val defaultOnePieceTokens = setOf(
		"aubergine",
		"aubergines",
		"avocado",
		"avocados",
		"broccoli",
		"broccolis",
		"butternut",
		"cabbage",
		"cabbages",
		"carrot",
		"carrots",
		"cauliflower",
		"cauliflowers",
		"celery",
		"chilli",
		"chillies",
		"chili",
		"chilies",
		"courgette",
		"courgettes",
		"cucumber",
		"cucumbers",
		"eggplant",
		"eggplants",
		"jalapeno",
		"jalapenos",
		"leek",
		"leeks",
		"lemon",
		"lemons",
		"lettuce",
		"lime",
		"limes",
		"mango",
		"onion",
		"onions",
		"orange",
		"oranges",
		"plantain",
		"potato",
		"potatoes",
		"shallot",
		"shallots",
		"squash",
		"tomato",
		"tomatoes",
		"tortilla",
		"tortillas",
		"zucchini",
		"zucchinis",
	)

	private val defaultTeaspoonSeasoningTokens = setOf(
		"salt",
		"peppercorns",
		"cayenne",
		"paprika",
		"nutmeg",
		"cinnamon",
		"cumin",
		"oregano",
		"thyme",
		"saffron",
		"sumac",
		"turmeric",
		"cardamom",
		"cloves",
		"basil",
		"parsley",
		"cilantro",
		"coriander",
		"mint",
		"rosemary",
		"sage",
		"tarragon",
		"dill",
		"chive",
		"chives",
		"bay",
		"star",
		"anise",
	)

	private val defaultTablespoonOilTokens = setOf(
		"oil",
		"oils",
		"spray",
	)

	private val defaultCupPantryTokens = setOf(
		"flour",
		"chickpeas",
		"chickpea",
		"beans",
		"bean",
		"rice",
		"sugar",
	)

	private val defaultTablespoonCreamTokens = setOf(
		"cream",
		"yoghurt",
		"yogurt",
		"milk",
		"butter",
		"mayo",
		"mayonnaise",
		"ketchup",
		"mustard",
		"salsa",
		"chutney",
	)

	private val defaultPieceHerbTokens = setOf(
		"ginger",
		"anchovy",
		"anchovies",
		"chorizo",
		"bacon",
		"sausage",
		"sausages",
		"scallion",
		"scallions",
		"apple",
		"baguette",
		"bun",
		"buns",
		"pitta",
		"pita",
		"egg",
		"eggs",
		"cube",
		"cubes",
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
		rest = stripLeadingOf(rest)
		rest = stripLeadingParenthetical(rest)
		rest = stripContainerWords(rest)

		val implied = resolveImpliedMeasure(quantity = quantity, unit = unit, rest = rest)
		quantity = implied.quantity
		unit = implied.unit
		rest = implied.rest
		if (unit == null) {
			unit = inferCountUnit(rest, hasQuantity = quantity != null)
		}
		val normalizedAmount = normalizeInformalUnits(quantity = quantity, unit = unit)
		quantity = normalizedAmount.quantity
		unit = normalizedAmount.unit
		if (unit == null) {
			val defaults = defaultUnquantifiedMeasure(rest)
			if (defaults.unit != null) {
				quantity = quantity ?: defaults.quantity
				unit = defaults.unit
			}
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
		val leadingWordQuantity = consumeLeadingWordQuantity(value)
		val labeledPack = consumeLabeledPack(leadingWordQuantity.rest)
		val leadingQuantity = leadingQuantityPattern.find(leadingWordQuantity.rest)
		val digitQuantity = leadingQuantity?.let { match -> parseQuantity(match.groupValues[1]) }
		val quantity = digitQuantity ?: leadingWordQuantity.quantity
		val afterQuantity = if (leadingQuantity == null) {
			leadingWordQuantity.rest
		} else {
			leadingWordQuantity.rest.substring(leadingQuantity.range.last + 1).trim()
		}
		val timesPack = consumePackQuantity(
			value = afterQuantity,
			allowImplicit = quantity != null,
		)
		return when {
			labeledPack != null -> LeadingAmount(
				quantity = (leadingWordQuantity.quantity ?: BigDecimal.ONE).multiply(labeledPack.quantity),
				unit = labeledPack.unit,
				rest = labeledPack.rest,
			)

			timesPack != null -> LeadingAmount(
				quantity = (quantity ?: BigDecimal.ONE).multiply(timesPack.quantity),
				unit = timesPack.unit,
				rest = timesPack.rest,
			)

			else -> LeadingAmount(
				quantity = quantity,
				unit = null,
				rest = afterQuantity,
			)
		}
	}

	private fun consumeLeadingWordQuantity(value: String): LeadingAmount {
		val token = firstToken(value).lowercase()
		val quantity = wordQuantities[token] ?: return LeadingAmount(
			quantity = null,
			unit = null,
			rest = value,
		)
		return LeadingAmount(
			quantity = quantity,
			unit = null,
			rest = dropFirstWord(value),
		)
	}

	private fun consumeLabeledPack(value: String): PackQuantity? {
		val match = hyphenatedPackPattern.find(value) ?: spacedPackPattern.find(value) ?: return null
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

	private fun stripLeadingOf(value: String): String {
		val trimmed = value.trim()
		return if (firstToken(trimmed).lowercase() == "of") {
			dropFirstWord(trimmed)
		} else {
			trimmed
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

	private fun normalizeInformalUnits(
		quantity: BigDecimal?,
		unit: String?,
	): LeadingAmount {
		val amount = quantity ?: BigDecimal.ONE
		return when {
			unit == null -> LeadingAmount(quantity = quantity, unit = null, rest = "")
			unit in pinchUnits -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("0.25")),
				unit = "tsp",
				rest = "",
			)

			unit in handfulUnits -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("2")),
				unit = "tbsp",
				rest = "",
			)

			else -> LeadingAmount(quantity = quantity, unit = unit, rest = "")
		}
	}

	private fun defaultUnquantifiedMeasure(rest: String): LeadingAmount {
		val lookup = NutritionNameNormalizer.forLookup(rest)
		val lookupTokens = lookup
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		return when {
			lookupTokens.isEmpty() ->
				LeadingAmount(quantity = null, unit = null, rest = rest)

			lookupTokens.any { token -> token == "juice" || token == "zest" } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "tbsp", rest = rest)

			lookupTokens.any { token -> token in defaultTablespoonOilTokens } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "tbsp", rest = rest)

			lookupTokens.any { token -> token in defaultTablespoonCreamTokens } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "tbsp", rest = rest)

			isSpiceSeasoning(lookupTokens) ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "tsp", rest = rest)

			lookupTokens.any { token -> token in defaultCupPantryTokens } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "cup", rest = rest)

			lookupTokens.any { token -> token in defaultPieceHerbTokens } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "piece", rest = rest)

			lookupTokens.any { token -> token in defaultOnePieceTokens } ->
				LeadingAmount(quantity = BigDecimal.ONE, unit = "piece", rest = rest)

			else -> LeadingAmount(quantity = BigDecimal.ONE, unit = "tsp", rest = rest)
		}
	}

	private fun isSpiceSeasoning(lookupTokens: List<String>): Boolean {
		if (lookupTokens.any { token -> token in defaultTeaspoonSeasoningTokens }) {
			return true
		}
		if (lookupTokens.none { token -> token == "pepper" }) {
			return false
		}
		val spicePepperModifiers = setOf("black", "white", "ground", "flakes", "flake", "crushed")
		return lookupTokens.size == 1 ||
			lookupTokens.any { token -> token in spicePepperModifiers } ||
			lookupTokens.any { token -> token == "salt" }
	}

	private fun findParentheticalMeasure(value: String): ParsedIngredientLine? {
		parentheticalPattern.findAll(value).forEach { match ->
			val inner = canonicalizeLine(match.groupValues[1])
			val innerMatch = aboutAmountPattern.find(inner) ?: innerAmountPattern.find(inner) ?: return@forEach
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

	private fun findTrailingAboutMeasure(value: String): ParsedIngredientLine? {
		val match = trailingAboutAmountPattern.find(value) ?: return null
		val quantity = parseQuantity(match.groupValues[1])
		val unit = normalizeUnit(match.groupValues[2])
		val usable = quantity != null &&
			unit != null &&
			(unit in consumedUnits || unit in knownUnits)
		return if (!usable) {
			null
		} else {
			val parsedName = value.replaceRange(match.range, " ").replace(extraWhitespacePattern, " ").trim()
			ParsedIngredientLine(
				rawText = value,
				quantity = quantity,
				unit = unit,
				parsedName = parsedName.ifBlank { value },
				isMeasurable = true,
			)
		}
	}

	private fun resolveImpliedMeasure(
		quantity: BigDecimal?,
		unit: String?,
		rest: String,
	): LeadingAmount {
		val parentheticalMeasure = if (unit == null) {
			findParentheticalMeasure(rest)
		} else {
			null
		}
		val afterParenthetical = if (parentheticalMeasure != null) {
			LeadingAmount(
				quantity = parentheticalMeasure.quantity,
				unit = parentheticalMeasure.unit,
				rest = parentheticalMeasure.parsedName,
			)
		} else {
			LeadingAmount(quantity = quantity, unit = unit, rest = rest)
		}
		val trailingMeasure = if (afterParenthetical.unit == null) {
			findTrailingAboutMeasure(afterParenthetical.rest)
		} else {
			null
		}
		val afterTrailing = if (trailingMeasure != null) {
			LeadingAmount(
				quantity = trailingMeasure.quantity,
				unit = trailingMeasure.unit,
				rest = trailingMeasure.parsedName,
			)
		} else {
			afterParenthetical
		}
		val countNoun = if (afterTrailing.unit == null && afterTrailing.quantity != null) {
			consumeCountNounMeasure(afterTrailing.rest)
		} else {
			null
		}
		return if (countNoun != null) {
			LeadingAmount(
				quantity = afterTrailing.quantity,
				unit = countNoun.unit,
				rest = countNoun.rest,
			)
		} else {
			afterTrailing
		}
	}

	private fun consumeCountNounMeasure(value: String): LeadingAmount? {
		val trimmed = value.trim()
		return when {
			trimmed.isEmpty() -> null
			firstToken(trimmed).lowercase() in countNounTokens -> {
				val noun = firstToken(trimmed).lowercase()
				var rest = dropFirstWord(trimmed)
				if (firstToken(rest).lowercase() == "of") {
					rest = dropFirstWord(rest)
				}
				LeadingAmount(
					quantity = null,
					unit = unitForCountNoun(noun),
					rest = bayLeafName(rest),
				)
			}

			else -> trailingCountNounMeasure(trimmed)
		}
	}

	private fun trailingCountNounMeasure(value: String): LeadingAmount? {
		val tokens = value.split(extraWhitespacePattern)
		if (tokens.size < 2) {
			return null
		}
		val trailing = tokens.last().lowercase().trimEnd(',', ';', '.')
		return if (trailing in countNounTokens) {
			LeadingAmount(
				quantity = null,
				unit = unitForCountNoun(trailing),
				rest = bayLeafName(tokens.dropLast(1).joinToString(" ")),
			)
		} else {
			null
		}
	}

	private fun unitForCountNoun(noun: String): String =
		when (noun) {
			in teaspoonCountNounTokens -> "tsp"
			in tablespoonCountNounTokens -> "tbsp"
			else -> "piece"
		}

	private fun bayLeafName(value: String): String {
		val trimmed = value.trim()
		return if (trimmed.equals("bay", ignoreCase = true)) {
			"bay leaf"
		} else {
			trimmed
		}
	}

	private fun inferCountUnit(parsedName: String, hasQuantity: Boolean): String? {
		val first = normalizeUnit(firstToken(parsedName))
		val lookupTokens = NutritionNameNormalizer.forLookup(parsedName)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		val isJuiceOrZest = lookupTokens.any { token -> token == "juice" || token == "zest" }
		val isGarlicClove = lookupTokens.any { token -> token == "garlic" } &&
			lookupTokens.any { token -> token.startsWith("clove") }
		val isBellPepper = lookupTokens.any { token -> token == "bell" } &&
			lookupTokens.any { token -> token == "pepper" }
		val isColoredPepper = lookupTokens.any { token -> token == "pepper" } &&
			lookupTokens.any { token -> token == "red" || token == "green" || token == "yellow" } &&
			lookupTokens.none { token ->
				token == "black" || token == "white" || token == "flakes" || token == "flake" || token == "crushed"
			}
		val isCornOnTheCob = lookupTokens.any { token -> token == "corn" } &&
			lookupTokens.any { token -> token == "cob" }
		val isProduce = !isJuiceOrZest &&
			(
				lookupTokens.any { token -> token in defaultOnePieceTokens } ||
					isBellPepper ||
					isColoredPepper ||
					isCornOnTheCob
				)
		val isCountableMeat = hasQuantity && isCountableMeatName(lookupTokens)
		return when {
			first == "egg" || first == "clove" || first == "piece" -> first
			isGarlicClove -> "clove"
			isProduce -> "piece"
			isCountableMeat -> "piece"
			else -> null
		}
	}

	private fun isCountableMeatName(lookupTokens: List<String>): Boolean {
		if (lookupTokens.any { token -> token in countableSausageTokens }) {
			return true
		}
		val hasChicken = lookupTokens.any { token -> token == "chicken" }
		val hasBreastOrThigh = lookupTokens.any { token -> token in countableMeatTokens }
		return hasChicken && hasBreastOrThigh
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
			"pinch", "pinches" -> "pinch"
			"dash", "dashes" -> "dash"
			"handful", "handfuls" -> "handful"
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

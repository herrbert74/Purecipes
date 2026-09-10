package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.math.RoundingMode

internal object IngredientLineParser {

	private const val QUANTITY_PATTERN = """\d+\s+\d+\s*/\s*\d+|\d+\s*/\s*\d+|\d+(?:\.\d+)?|\.\d+"""
	private const val QUANTITY_SCALE = 4
	private const val REGEX_GROUP_FIRST = 1
	private const val REGEX_GROUP_SECOND = 2
	private const val REGEX_GROUP_THIRD = 3
	private const val REGEX_GROUP_FOURTH = 4
	private const val COUPLE_COUNT = 2

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
		"""^\(($QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*(?:;[^)]*)?\)""",
		RegexOption.IGNORE_CASE,
	)

	private val leadingParentheticalPattern = Regex("""^\([^)]*\)\s*""")

	private val parentheticalPattern = Regex("""\(([^)]+)\)""")

	private val innerAmountPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*(?:;.*)?$""",
		RegexOption.IGNORE_CASE,
	)

	private val innerRangeAmountPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*(?:to|-)\s*(?:$QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val mixedNumberPattern = Regex("""^(\d+)\s+(\d+)\s*/\s*(\d+)$""")

	private val simpleFractionPattern = Regex("""^(\d+)\s*/\s*(\d+)$""")

	private val extraWhitespacePattern = Regex("""\s+""")

	private val quantityRangeTailPattern = Regex(
		"""^(?:to|-)\s*(?:$QUANTITY_PATTERN)\s+""",
		RegexOption.IGNORE_CASE,
	)

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

	private val rangedPackPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-\s*to\s*($QUANTITY_PATTERN)\s*-?\s*(ounces?|oz|pounds?|lbs?)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val spacedPackPattern = Regex(
		"""^($QUANTITY_PATTERN)\s+(ounces?|oz|pounds?|lbs?)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalPackAmountPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s+""" +
			"""([a-zA-Z][a-zA-Z.\-]*)\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalAmountWithTotalPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s*(?:total|each)\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val unopenedLabelPattern = Regex(
		"""(?i)(?:,\s*)?unopened\b(?:\s*,?\s*labels?\s+removed)?""",
	)

	private val parentheticalNoiseFoodTokens = setOf(
		"about",
		"approximately",
		"approx",
		"each",
		"total",
	)

	private val aboutAmountPattern = Regex(
		"""^(?:total\s+weight\s+)?(?:about|approximately|approx\.?)\s+($QUANTITY_PATTERN)\s*-?\s*""" +
			"""([a-zA-Z][a-zA-Z.\-]*)\.?\s*(?:each|total)?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val trailingAboutAmountPattern = Regex(
		"""(?:,\s*)?(?:about|approximately|approx\.?)\s+($QUANTITY_PATTERN)\s*-?\s*""" +
			"""([a-zA-Z][a-zA-Z.\-]*)\.?\s*(?:total)?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val trailingMassAmountPattern = Regex(
		"""\s+($QUANTITY_PATTERN)\s*(${IngredientLineParserLexicon.TRAILING_MASS_UNITS})?\.?\s*$""",
		RegexOption.IGNORE_CASE,
	)

	private val seedsFromVanillaPodPattern = Regex(
		"""^seeds\s+from\s+($QUANTITY_PATTERN)\s+vanilla\s+pods?\b.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val peelFromCitrusPattern = Regex(
		"""(?i)^peel\b.*\bfrom\b.*\b(lemon|lime|orange|grapefruit)s?\b.*$""",
	)

	private val suchAsClausePattern = Regex(
		"""(?i)^.*\bsuch as\s+(.+)$""",
	)

	private val coupleOfPattern = Regex(
		"""^(?:a\s+)?couple\s+of\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val leadingMeasurePlusPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s+plus\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalMassInPlusPattern = Regex(
		"""\(([^)]*?)($QUANTITY_PATTERN)\s*(g|kg|ml|l|oz|lb)\b[^)]*\)""",
		RegexOption.IGNORE_CASE,
	)

	private val quantityParenFoodPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*\(($QUANTITY_PATTERN)\s*-?\s*""" +
			"""([a-zA-Z][a-zA-Z.\-]*)\.?\s+([^)]+)\)\s*(?:cut\s+into\b.*)?$""",
		RegexOption.IGNORE_CASE,
	)

	private val leadingDimensionPattern = Regex(
		"""^\d+(?:\s+\d+\s*/\s*\d+|\s*/\s*\d+|\.\d+)?\s*-?\s*inch(?:es)?\b""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalFoodAmountPattern = Regex(
		"""^($QUANTITY_PATTERN)\s*-?\s*([a-zA-Z][a-zA-Z.\-]*)\.?\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val timesMarkerTokens = setOf("x", "×", "*")

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

		var rest = canonicalizeLine(rewriteSpecialPhrases(rawText))
		val leadingAmount = consumeLeadingQuantityAndPack(rest)
		var quantity = leadingAmount.quantity
		var unit = leadingAmount.unit
		rest = leadingAmount.rest

		rest = skipSizeTokens(rest)
		rest = skipRecipeToken(rest)
		if (unit == null) {
			val consumed = consumeUnit(rest)
			if (consumed != null) {
				unit = consumed.first
				rest = consumed.second
			}
		}
		rest = stripLeadingOf(rest)
		val implied = resolveImpliedMeasure(quantity = quantity, unit = unit, rest = rest)
		quantity = implied.quantity
		unit = implied.unit
		rest = stripLeadingParenthetical(implied.rest)
		rest = stripContainerWords(rest)
		rest = stripLeftoverAmounts(rest, quantityAlreadySet = quantity != null)
		rest = stripOrphanUnitTokens(rest)
		rest = stripParentheticalClauses(rest)
		rest = stripUnopenedLabelNoise(rest)
		rest = stripRedundantFromClause(rest)
		rest = recoverFoodNameIfOrphanCollapsed(rest, rawText)
		if (unit == null) {
			unit = inferCountUnit(rest, hasQuantity = quantity != null)
		}
		val normalizedAmount = normalizeInformalUnits(quantity = quantity, unit = unit)
		quantity = normalizedAmount.quantity
		unit = normalizedAmount.unit
		if (unit == null && NutritionNameNormalizer.hasMeaningfulFoodName(rest)) {
			val defaults = defaultUnquantifiedMeasure(rest)
			if (defaults.unit != null) {
				quantity = quantity ?: defaults.quantity
				unit = defaults.unit
			}
		}
		if (quantity == null && unit in IngredientLineParserLexicon.defaultSingleCountUnits) {
			quantity = BigDecimal.ONE
		}

		rest = bayLeafName(rest)
		val parsedName = resolvedParsedName(unit = unit, rest = rest, rawText = rawText)
		val isMeasurable = quantity != null &&
			unit != null &&
			unit in IngredientLineParserLexicon.knownUnits &&
			NutritionNameNormalizer.hasMeaningfulFoodName(parsedName)
		return ParsedIngredientLine(
			rawText = rawText,
			quantity = quantity,
			unit = unit,
			parsedName = parsedName,
			isMeasurable = isMeasurable,
		)
	}

	private fun rewriteSpecialPhrases(value: String): String {
		val trimmed = value.trim()
		val seeds = seedsFromVanillaPodPattern.matchEntire(trimmed)
		val peel = peelFromCitrusPattern.find(trimmed)
		val suchAs = suchAsClausePattern.find(trimmed)
		val couple = coupleOfPattern.matchEntire(trimmed)
		val plus = leadingMeasurePlusPattern.matchEntire(trimmed)
		val parenFood = quantityParenFoodPattern.matchEntire(trimmed)
		return when {
			seeds != null -> "${seeds.groupValues[REGEX_GROUP_FIRST]} vanilla pod"
			peel != null -> "${peel.groupValues[REGEX_GROUP_FIRST]} peel"
			parenFood != null -> listOf(
				parenFood.groupValues[REGEX_GROUP_SECOND],
				parenFood.groupValues[REGEX_GROUP_THIRD],
				parenFood.groupValues[REGEX_GROUP_FOURTH],
			).joinToString(" ").trim()

			plus != null -> rewritePlusClause(plus.groupValues[REGEX_GROUP_THIRD].trim())
			couple != null -> "$COUPLE_COUNT ${couple.groupValues[REGEX_GROUP_FIRST].trim()}"
			suchAs != null && looksLikeVagueLeadIn(value) -> suchAs.groupValues[REGEX_GROUP_FIRST].trim()
			else -> value
		}
	}

	private fun rewritePlusClause(tail: String): String {
		val mass = parentheticalMassInPlusPattern.find(tail) ?: return tail
		val food = extraWhitespacePattern
			.replace(tail.replaceRange(mass.range, " "), " ")
			.trim()
			.trimStart(',')
			.trim()
		return if (food.isBlank()) {
			tail
		} else {
			listOf(
				mass.groupValues[REGEX_GROUP_SECOND],
				mass.groupValues[REGEX_GROUP_THIRD],
				food,
			).joinToString(" ")
		}
	}

	private fun looksLikeVagueLeadIn(value: String): Boolean {
		val lower = value.lowercase()
		return lower.contains("something ") ||
			lower.contains("oniony") ||
			lower.contains("of your choosing") ||
			lower.contains("of your choice")
	}

	private fun canonicalizeLine(value: String): String {
		val replaced = buildString {
			value.forEach { character ->
				when {
					character == '–' || character == '—' -> append('-')
					else -> {
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
			}
		}
		return extraWhitespacePattern.replace(replaced, " ").trim()
	}

	private fun consumeLeadingQuantityAndPack(value: String): LeadingAmount {
		val withoutDimensions = skipLeadingDimensions(value)
		val leadingParenthetical = leadingParentheticalAmountPattern.find(withoutDimensions)
		if (leadingParenthetical != null) {
			return LeadingAmount(
				quantity = parseQuantity(leadingParenthetical.groupValues[1]),
				unit = normalizeUnit(leadingParenthetical.groupValues[2]),
				rest = withoutDimensions.substring(leadingParenthetical.range.last + 1).trim(),
			)
		}
		val leadingWordQuantity = consumeLeadingWordQuantity(withoutDimensions)
		val afterWord = skipLeadingDimensions(leadingWordQuantity.rest)
		val labeledPack = consumeLabeledPack(afterWord)
		val leadingQuantity = leadingQuantityPattern.find(afterWord)
		val digitQuantity = leadingQuantity?.let { match -> parseQuantity(match.groupValues[1]) }
		val quantity = digitQuantity ?: leadingWordQuantity.quantity
		val afterQuantity = if (leadingQuantity == null) {
			afterWord
		} else {
			afterWord.substring(leadingQuantity.range.last + 1).trim()
		}
		val afterRange = stripLeadingTimesMarker(stripQuantityRangeTail(skipLeadingDimensions(afterQuantity)))
		val timesPack = consumePackQuantity(
			value = afterRange,
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
				rest = afterRange,
			)
		}
	}

	private fun skipLeadingDimensions(value: String): String {
		val trimmed = value.trim()
		val match = leadingDimensionPattern.find(trimmed)
		if (match == null || match.range.first != 0) {
			return trimmed
		}
		return skipLeadingDimensions(trimmed.substring(match.range.last + 1))
	}

	private fun stripQuantityRangeTail(value: String): String {
		val match = quantityRangeTailPattern.find(value.trim()) ?: return value.trim()
		return value.trim().substring(match.range.last + 1).trim()
	}

	private fun stripLeadingTimesMarker(value: String): String {
		val trimmed = value.trim()
		val token = firstToken(trimmed).lowercase()
		return if (token in timesMarkerTokens) {
			dropFirstWord(trimmed)
		} else {
			trimmed
		}
	}

	private fun consumeLeadingWordQuantity(value: String): LeadingAmount {
		val token = firstToken(value).lowercase()
		val quantity = IngredientLineParserLexicon.wordQuantities[token]
			?: IngredientLineParserTokens.extraWordQuantities[token]
			?: return LeadingAmount(
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
		val ranged = rangedPackPattern.find(value)
		val match = ranged
			?: hyphenatedPackPattern.find(value)
			?: spacedPackPattern.find(value)
			?: return null
		val unitGroup = if (ranged != null) REGEX_GROUP_THIRD else REGEX_GROUP_SECOND
		val quantity = parseQuantity(match.groupValues[REGEX_GROUP_FIRST])
		val unit = normalizeUnit(match.groupValues[unitGroup])
		return if (quantity != null && unit != null && unit in IngredientLineParserLexicon.packUnits) {
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
		val labeled = consumeLabeledPack(value)
		val match = packTimesPattern.find(value)
			?: if (allowImplicit) implicitPackPattern.find(value) else null
		val quantity = match?.let { parseQuantity(it.groupValues[1]) }
		val resolvedUnit = match
			?.let { normalizeUnit(it.groupValues[2]) }
			?.takeIf { unit -> unit in IngredientLineParserLexicon.packUnits }
		val fromPattern = if (match != null && quantity != null && resolvedUnit != null) {
			PackQuantity(
				quantity = quantity,
				unit = resolvedUnit,
				rest = value.substring(match.range.last + 1).trim(),
			)
		} else {
			null
		}
		return labeled ?: fromPattern
	}

	private fun skipSizeTokens(value: String): String {
		var rest = value.trim()
		while (rest.isNotEmpty()) {
			val dimension = leadingDimensionPattern.find(rest)
			if (dimension != null && dimension.range.first == 0) {
				rest = rest.substring(dimension.range.last + 1).trim()
				continue
			}
			val token = firstToken(rest).lowercase()
			if (token !in IngredientLineParserLexicon.sizeTokens) {
				return rest
			}
			rest = dropFirstWord(rest)
		}
		return rest
	}

	private fun skipRecipeToken(value: String): String {
		val token = firstToken(value).lowercase()
		return if (token == "recipe") {
			dropFirstWord(value)
		} else {
			value.trim()
		}
	}

	private fun consumeUnit(value: String): Pair<String, String>? {
		val token = firstToken(value)
		val unit = normalizeUnit(token)
		return if (token.isNotEmpty() && unit != null && unit in IngredientLineParserLexicon.consumedUnits) {
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

	private fun stripLeftoverAmounts(value: String, quantityAlreadySet: Boolean): String {
		var rest = value.trim()
		if (quantityAlreadySet) {
			rest = stripLeadingBareNumber(rest)
		}
		return stripTrailingMassAmount(rest)
	}

	private fun stripLeadingBareNumber(value: String): String {
		val match = leadingQuantityPattern.find(value.trim()) ?: return value.trim()
		val afterNumber = value.trim().substring(match.range.last + 1).trim()
		val next = firstToken(afterNumber).lowercase()
		return if (afterNumber.isNotEmpty() && normalizeUnit(next) == null) {
			afterNumber
		} else {
			value.trim()
		}
	}

	private fun stripTrailingMassAmount(value: String): String {
		val match = trailingMassAmountPattern.find(value.trim()) ?: return value.trim()
		val unit = normalizeUnit(match.groupValues[2].ifBlank { null })
		val bareNumber = match.groupValues[2].isBlank()
		return if (bareNumber || unit in IngredientLineParserLexicon.packUnits) {
			value.trim().replaceRange(match.range, " ").replace(extraWhitespacePattern, " ").trim()
		} else {
			value.trim()
		}
	}

	private fun stripOrphanUnitTokens(value: String): String {
		var rest = value.trim()
		while (rest.isNotEmpty()) {
			val stripped = stripOneOrphanUnitToken(rest) ?: break
			rest = stripped
		}
		return rest
	}

	private fun stripOneOrphanUnitToken(value: String): String? {
		val tokens = value.split(extraWhitespacePattern)
		if (tokens.size < 2) {
			return null
		}
		val leading = tokens.first().lowercase().trimEnd(',', ';', '.')
		val trailing = tokens.last().lowercase().trimEnd(',', ';', '.')
		return when {
			isOrphanMeasureUnit(leading) -> tokens.drop(1).joinToString(" ")
			isOrphanMeasureUnit(trailing) -> tokens.dropLast(1).joinToString(" ")
			else -> null
		}
	}

	private fun isOrphanMeasureUnit(token: String): Boolean {
		val unit = normalizeUnit(token)
		return unit != null &&
			unit != "clove" &&
			(unit in IngredientLineParserLexicon.consumedUnits || unit in IngredientLineParserLexicon.packUnits)
	}

	private fun stripLeadingParenthetical(value: String): String =
		leadingParentheticalPattern.replaceFirst(value, "").trim()

	private fun stripContainerWords(value: String): String {
		val token = firstToken(value).lowercase()
		if (token !in IngredientLineParserLexicon.containerTokens) {
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
			unit in IngredientLineParserLexicon.pinchUnits -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("0.25")),
				unit = "tsp",
				rest = "",
			)

			unit in IngredientLineParserLexicon.handfulUnits -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("2")),
				unit = "tbsp",
				rest = "",
			)

			unit == "pint" -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("2")),
				unit = "cup",
				rest = "",
			)

			unit == "quart" -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("4")),
				unit = "cup",
				rest = "",
			)

			unit == "gallon" -> LeadingAmount(
				quantity = amount.multiply(BigDecimal("16")),
				unit = "cup",
				rest = "",
			)

			else -> LeadingAmount(quantity = quantity, unit = unit, rest = "")
		}
	}

	private fun defaultUnquantifiedMeasure(rest: String): LeadingAmount {
		val lookupTokens = NutritionNameNormalizer.forLookup(rest)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		if (lookupTokens.isEmpty() || isOrphanModifierName(lookupTokens)) {
			return LeadingAmount(quantity = null, unit = null, rest = rest)
		}
		val unit = defaultUnquantifiedUnit(rest = rest, lookupTokens = lookupTokens)
		return if (unit == null) {
			LeadingAmount(quantity = null, unit = null, rest = rest)
		} else {
			val measuredRest = if (unit == "tsp" && isBayLeafName(rest, lookupTokens)) {
				bayLeafName(rest)
			} else {
				rest
			}
			LeadingAmount(quantity = BigDecimal.ONE, unit = unit, rest = measuredRest)
		}
	}

	private fun defaultUnquantifiedUnit(rest: String, lookupTokens: List<String>): String? = when {
		isBayLeafName(rest, lookupTokens) -> "tsp"
		lookupTokens.any { token -> token == "juice" || token == "zest" || token == "peel" } -> "tbsp"
		isFreshGarlic(lookupTokens) -> "clove"
		isDefaultTablespoonFood(lookupTokens) -> "tbsp"
		isSpiceSeasoning(lookupTokens) ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultTeaspoonPantryTokens } -> "tsp"

		isCupOfStockOrBroth(lookupTokens) || isDefaultCupFood(lookupTokens) -> "cup"
		isDefaultPieceFood(lookupTokens) -> "piece"
		else -> null
	}

	private fun isDefaultTablespoonFood(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultTablespoonOilTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultTablespoonCreamTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultTablespoonSauceTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultTablespoonDrinkTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultTablespoonNutSeedTokens }

	private fun isDefaultCupFood(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultCupPantryTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultCupGrainBeanTokens } ||
			isBareCupLiquid(lookupTokens)

	private fun isDefaultPieceFood(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultPieceHerbTokens } ||
			isHotDog(lookupTokens) ||
			isDefaultablePoultry(lookupTokens) ||
			isCountableMeatName(lookupTokens) ||
			isBellPepper(lookupTokens) ||
			isColoredOrSweetPepper(lookupTokens) ||
			isCornOnTheCob(lookupTokens) ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceProteinTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceCheeseTokens } ||
			(
				lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceBreadTokens } &&
					lookupTokens.none { token -> token == "garlic" }
				) ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPiecePepperTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPiecePreparedTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultOnePieceTokens }

	private fun isOrphanModifierName(lookupTokens: List<String>): Boolean =
		lookupTokens.isNotEmpty() &&
			lookupTokens.all { token ->
				token in IngredientLineParserTokens.orphanModifierOnlyTokens
			}

	private fun recoverFoodNameIfOrphanCollapsed(rest: String, rawText: String): String {
		val restTokens = NutritionNameNormalizer.forLookup(rest)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		if (!isOrphanModifierName(restTokens)) {
			return rest
		}
		val rawLookup = NutritionNameNormalizer.forLookup(rawText)
		val rawTokens = rawLookup
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		return if (rawTokens.isEmpty() || isOrphanModifierName(rawTokens)) {
			rest
		} else {
			rawLookup
		}
	}

	private fun isBareCupLiquid(lookupTokens: List<String>): Boolean {
		if (lookupTokens.isEmpty()) {
			return false
		}
		val liquidTokens = lookupTokens.filter { token ->
			token in IngredientLineParserTokens.defaultCupLiquidTokens
		}
		val nonLiquid = lookupTokens.filterNot { token ->
			token in IngredientLineParserTokens.defaultCupLiquidTokens ||
				token == "shaking" ||
				token == "brewed" ||
				token == "black"
		}
		return liquidTokens.isNotEmpty() && nonLiquid.isEmpty()
	}

	private fun isBayLeafName(rest: String, lookupTokens: List<String>): Boolean {
		val compact = rest.lowercase().replace(" ", "")
		val hasBayLeafTokens = lookupTokens.any { token -> token == "bay" } &&
			lookupTokens.any { token -> token == "leaf" || token == "leaves" }
		return compact == "bayleaf" || compact == "bayleaves" || hasBayLeafTokens
	}

	private fun isDefaultablePoultry(lookupTokens: List<String>): Boolean {
		val hasPoultry = lookupTokens.any { token -> token in IngredientLineParserTokens.countablePoultryTokens }
		val hasBareCut = lookupTokens.any { token -> token in IngredientLineParserTokens.bareBreastOrThighTokens }
		val foodTokens =
			lookupTokens.filterNot { token -> token in IngredientLineParserTokens.poultryPrepModifierTokens }
		val isWholeOrCutPiece = foodTokens.size == 1 ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.wholeBirdTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.extraCountableMeatTokens } ||
			(
				hasPoultry &&
					foodTokens.all { token ->
						token in IngredientLineParserTokens.countablePoultryTokens ||
							token in IngredientLineParserTokens.poultryPrepModifierTokens
					}
				)
		return hasPoultry && !hasBareCut && isWholeOrCutPiece
	}

	private fun isSpiceSeasoning(lookupTokens: List<String>): Boolean {
		val spicePowderBases = setOf("garlic", "onion", "chili", "chilli", "chile")
		val spicePepperModifiers = setOf("black", "white", "ground", "flakes", "flake", "crushed")
		val hasSeasoningToken =
			lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultTeaspoonSeasoningTokens }
		val hasWholeCloveSpice = isWholeCloveSpice(lookupTokens)
		val hasSpicePowder = lookupTokens.any { token -> token == "powder" } &&
			lookupTokens.any { token -> token in spicePowderBases }
		val isPepperSeasoning = isPepperSeasoning(lookupTokens, spicePepperModifiers)
		val hasAllspice = lookupTokens.any { token -> token == "allspice" }
		val hasCurry = lookupTokens.any { token -> token == "curry" }
		return hasSeasoningToken ||
			hasWholeCloveSpice ||
			hasSpicePowder ||
			isPepperSeasoning ||
			hasAllspice ||
			hasCurry
	}

	private fun isWholeCloveSpice(lookupTokens: List<String>): Boolean {
		val hasClove = lookupTokens.any { token -> token == "clove" || token == "cloves" }
		val hasGarlic = lookupTokens.any { token -> token == "garlic" }
		val onlyCloveSpiceTokens = lookupTokens.all { token ->
			token == "clove" || token == "cloves" || token == "whole" || token == "ground"
		}
		return hasClove && !hasGarlic && (
			lookupTokens.any { token -> token == "whole" } || onlyCloveSpiceTokens
			)
	}

	private fun isPepperSeasoning(
		lookupTokens: List<String>,
		spicePepperModifiers: Set<String>,
	): Boolean {
		val hasPepper = lookupTokens.any { token -> token == "pepper" || token == "peppers" }
		val producePepper = isBellPepper(lookupTokens) || isColoredOrSweetPepper(lookupTokens)
		val hasSpiceCue = lookupTokens.size == 1 ||
			lookupTokens.any { token -> token in spicePepperModifiers } ||
			lookupTokens.any { token -> token == "salt" }
		return hasPepper && (!producePepper || hasSpiceCue)
	}

	private fun isFreshGarlic(lookupTokens: List<String>): Boolean {
		val allowedGarlicTokens = setOf(
			"bulb",
			"bulbs",
			"clove",
			"cloves",
			"garlic",
			"head",
			"heads",
		)
		val hasGarlic = lookupTokens.any { token -> token == "garlic" }
		val isProcessed = lookupTokens.any { token -> token in IngredientLineParserLexicon.garlicProcessedTokens }
		val onlyGarlicTokens = lookupTokens.all { token ->
			token in allowedGarlicTokens || token.startsWith("clove")
		}
		return hasGarlic && !isProcessed && onlyGarlicTokens
	}

	private fun isHotDog(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token == "hotdog" || token == "hotdogs" } ||
			(
				lookupTokens.any { token -> token == "hot" } &&
					lookupTokens.any { token -> token == "dog" || token == "dogs" }
				)

	private fun isCupOfStockOrBroth(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token == "stock" || token == "broth" } &&
			lookupTokens.none { token -> token == "cube" || token == "cubes" }

	private fun findParentheticalMeasure(value: String): ParsedIngredientLine? {
		parentheticalPattern.findAll(value).forEach { match ->
			val inner = canonicalizeLine(match.groupValues[1])
			if (inner.contains("each", ignoreCase = true)) {
				return@forEach
			}
			val amount = parseParentheticalAmount(inner) ?: return@forEach
			val outerName = value.replaceRange(match.range, " ").replace(extraWhitespacePattern, " ").trim()
			val fromInner = amount.rest.trim()
			val parsedName = when {
				fromInner.isBlank() || isParentheticalNoiseFoodName(fromInner) -> outerName
				else -> fromInner
			}.ifBlank { value }
			return ParsedIngredientLine(
				rawText = value,
				quantity = amount.quantity,
				unit = amount.unit,
				parsedName = parsedName,
				isMeasurable = true,
			)
		}
		return null
	}

	private fun parseParentheticalAmount(inner: String): LeadingAmount? {
		val fromFoodOrPack = parseParentheticalFoodAmount(inner)
			?: parseParentheticalPackAmount(inner)
			?: parseParentheticalTotalAmount(inner)
		if (fromFoodOrPack != null) {
			return fromFoodOrPack
		}
		val semicolonInner = inner.substringBefore(';').trim()
		val aboutCandidate = if (semicolonInner.startsWith("total weight", ignoreCase = true)) {
			semicolonInner
		} else {
			inner
		}
		val match = aboutAmountPattern.find(aboutCandidate)
			?: innerRangeAmountPattern.find(inner)
			?: innerAmountPattern.find(inner)
			?: rangeOrAmountAfterSemicolon(inner, semicolonInner)
		val quantity = match?.let { parseQuantity(it.groupValues[REGEX_GROUP_FIRST]) }
		val unit = match?.let { normalizeUnit(it.groupValues[REGEX_GROUP_SECOND]) }
		val usable = quantity != null &&
			unit != null &&
			(unit in IngredientLineParserLexicon.consumedUnits || unit in IngredientLineParserLexicon.knownUnits)
		return if (usable) {
			LeadingAmount(quantity = quantity, unit = unit, rest = "")
		} else {
			null
		}
	}

	private fun parseParentheticalTotalAmount(inner: String): LeadingAmount? {
		val totalAmount = parentheticalAmountWithTotalPattern.matchEntire(inner) ?: return null
		val quantity = parseQuantity(totalAmount.groupValues[REGEX_GROUP_FIRST])
		val unit = normalizeUnit(totalAmount.groupValues[REGEX_GROUP_SECOND])
		val usable = quantity != null &&
			unit != null &&
			(unit in IngredientLineParserLexicon.consumedUnits || unit in IngredientLineParserLexicon.knownUnits)
		return if (usable) {
			LeadingAmount(quantity = quantity, unit = unit, rest = "")
		} else {
			null
		}
	}

	private fun parseParentheticalFoodAmount(inner: String): LeadingAmount? {
		val foodAmount = parentheticalFoodAmountPattern.matchEntire(inner) ?: return null
		val foodQuantity = parseQuantity(foodAmount.groupValues[REGEX_GROUP_FIRST])
		val foodUnit = normalizeUnit(foodAmount.groupValues[REGEX_GROUP_SECOND])
		val foodName = foodAmount.groupValues[REGEX_GROUP_THIRD].trim()
		val usableUnit = foodQuantity != null &&
			foodUnit != null &&
			(
				foodUnit in IngredientLineParserLexicon.consumedUnits ||
					foodUnit in IngredientLineParserLexicon.knownUnits
				)
		return when {
			!usableUnit -> null
			isParentheticalNoiseFoodName(foodName) -> LeadingAmount(
				quantity = foodQuantity,
				unit = foodUnit,
				rest = "",
			)

			foodName.isNotBlank() -> LeadingAmount(
				quantity = foodQuantity,
				unit = foodUnit,
				rest = foodName,
			)

			else -> null
		}
	}

	private fun parseParentheticalPackAmount(inner: String): LeadingAmount? {
		val packAmount = parentheticalPackAmountPattern.matchEntire(inner) ?: return null
		val quantity = parseQuantity(packAmount.groupValues[REGEX_GROUP_FIRST])
		val unit = normalizeUnit(packAmount.groupValues[REGEX_GROUP_SECOND])
		val container = packAmount.groupValues[REGEX_GROUP_THIRD].lowercase().trimEnd('.', ',')
		val usable = quantity != null &&
			unit != null &&
			container in IngredientLineParserLexicon.containerTokens &&
			(unit in IngredientLineParserLexicon.consumedUnits || unit in IngredientLineParserLexicon.knownUnits)
		return if (usable) {
			LeadingAmount(quantity = quantity, unit = unit, rest = "")
		} else {
			null
		}
	}

	private fun isParentheticalNoiseFoodName(value: String): Boolean {
		val tokens = value.lowercase()
			.split(extraWhitespacePattern)
			.map { token -> token.trimEnd(',', ';', '.') }
			.filter { token -> token.isNotEmpty() }
		return tokens.isNotEmpty() &&
			tokens.all { token ->
				token in parentheticalNoiseFoodTokens ||
					token in IngredientLineParserLexicon.containerTokens
			}
	}

	private fun rangeOrAmountAfterSemicolon(inner: String, semicolonInner: String): MatchResult? {
		if (semicolonInner == inner) {
			return null
		}
		val afterSemicolon = inner.substringAfter(';').trim()
		return innerRangeAmountPattern.find(afterSemicolon)
			?: innerAmountPattern.find(afterSemicolon)
			?: innerRangeAmountPattern.find(semicolonInner)
			?: innerAmountPattern.find(semicolonInner)
	}

	private fun stripParentheticalClauses(value: String): String =
		extraWhitespacePattern.replace(parentheticalPattern.replace(value, " "), " ").trim()

	private fun stripUnopenedLabelNoise(value: String): String =
		extraWhitespacePattern
			.replace(unopenedLabelPattern.replace(value, " "), " ")
			.trim()
			.trimEnd(',')
			.trim()

	private fun stripRedundantFromClause(value: String): String {
		val match = Regex("""^(.*?)\s+from\b\s+(.+)$""", RegexOption.IGNORE_CASE)
			.matchEntire(value.trim())
			?: return value.trim()
		val before = match.groupValues[REGEX_GROUP_FIRST].trim()
		val after = match.groupValues[REGEX_GROUP_SECOND].trim()
		val beforeLookup = NutritionNameNormalizer.forLookup(before)
		val afterLookup = NutritionNameNormalizer.forLookup(after)
		return if (
			beforeLookup.isNotBlank() &&
			afterLookup.isNotBlank() &&
			afterLookup.contains(beforeLookup)
		) {
			before
		} else {
			value.trim()
		}
	}

	private fun findTrailingAboutMeasure(value: String): ParsedIngredientLine? {
		val match = trailingAboutAmountPattern.find(value) ?: return null
		val quantity = parseQuantity(match.groupValues[1])
		val unit = normalizeUnit(match.groupValues[2])
		val usable = quantity != null &&
			unit != null &&
			(unit in IngredientLineParserLexicon.consumedUnits || unit in IngredientLineParserLexicon.knownUnits)
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
		if (unit == null && quantity != null) {
			val leadingCount = leadingCountNounMeasure(rest)
			if (leadingCount != null) {
				return LeadingAmount(
					quantity = quantity,
					unit = leadingCount.unit,
					rest = stripParentheticalClauses(leadingCount.rest),
				)
			}
		}
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
		val countNoun = when {
			afterTrailing.unit != null -> null
			afterTrailing.quantity != null -> consumeCountNounMeasure(stripParentheticalClauses(afterTrailing.rest))
			else -> {
				val withoutParentheticals = stripParentheticalClauses(afterTrailing.rest)
				leadingCountNounMeasure(withoutParentheticals)
					?: trailingCountNounMeasure(withoutParentheticals)
					?: internalCountNounMeasure(withoutParentheticals)
			}
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

	private fun leadingCountNounMeasure(value: String): LeadingAmount? {
		val trimmed = value.trim()
		val noun = firstToken(trimmed).lowercase()
		if (trimmed.isEmpty() || noun !in allCountNounTokens()) {
			return null
		}
		var rest = dropFirstWord(trimmed)
		if (firstToken(rest).lowercase() == "of") {
			rest = dropFirstWord(rest)
		}
		return LeadingAmount(
			quantity = null,
			unit = unitForCountNoun(noun),
			rest = bayLeafName(rest),
		)
	}

	private fun consumeCountNounMeasure(value: String): LeadingAmount? {
		val trimmed = value.trim()
		return when {
			trimmed.isEmpty() -> null
			firstToken(trimmed).lowercase() in allCountNounTokens() -> leadingCountNounMeasure(
				trimmed
			)

			else -> trailingCountNounMeasure(trimmed) ?: internalCountNounMeasure(trimmed)
		}
	}

	private fun trailingCountNounMeasure(value: String): LeadingAmount? {
		val tokens = value.split(extraWhitespacePattern)
		if (tokens.size < 2) {
			return null
		}
		val trailing = tokens.last().lowercase().trimEnd(',', ';', '.')
		return if (trailing in allCountNounTokens()) {
			LeadingAmount(
				quantity = null,
				unit = unitForCountNoun(trailing),
				rest = bayLeafName(tokens.dropLast(1).joinToString(" ")),
			)
		} else {
			null
		}
	}

	private fun internalCountNounMeasure(value: String): LeadingAmount? {
		val tokens = value.split(extraWhitespacePattern)
		val countNouns = allCountNounTokens()
		val index = tokens.indexOfLast { token ->
			token.lowercase().trimEnd(',', ';', '.') in countNouns
		}
		val nounInMiddle = tokens.size >= MIN_INTERNAL_COUNT_NOUN_TOKENS &&
			index > 0 &&
			index < tokens.lastIndex
		val noun = if (nounInMiddle) {
			tokens[index].lowercase().trimEnd(',', ';', '.')
		} else {
			null
		}
		val rest = noun?.let { countNoun ->
			countNounFoodName(
				noun = countNoun,
				before = tokens.take(index),
				after = tokens.drop(index + 1),
			).takeIf { foodName -> foodName.isNotBlank() }
		}
		return rest?.let { foodName ->
			LeadingAmount(
				quantity = null,
				unit = unitForCountNoun(noun.orEmpty()),
				rest = bayLeafName(foodName),
			)
		}
	}

	private fun countNounFoodName(
		noun: String,
		before: List<String>,
		after: List<String>,
	): String {
		val afterHasMignon = after.any { token -> token.equals("mignon", ignoreCase = true) }
		return when {
			afterHasMignon -> listOf("filet", "mignon").joinToString(" ")
			noun in IngredientLineParserTokens.volumeCountNounTokens -> (before + after).joinToString(" ")
			noun in IngredientLineParserTokens.cutCountNounTokens ||
				noun == "stalk" ||
				noun == "stalks" ||
				noun == "sheet" ||
				noun == "sheets" -> before.joinToString(" ")

			before.isNotEmpty() && after.isNotEmpty() -> (before + after).joinToString(" ")
			before.isNotEmpty() -> before.joinToString(" ")
			else -> after.joinToString(" ")
		}
	}

	private fun allCountNounTokens(): Set<String> =
		IngredientLineParserLexicon.countNounTokens + IngredientLineParserTokens.extraCountNounTokens

	private fun unitForCountNoun(noun: String): String =
		when (noun) {
			in IngredientLineParserLexicon.teaspoonCountNounTokens,
			in IngredientLineParserTokens.teaspoonExtraCountNounTokens,
				-> "tsp"

			in IngredientLineParserLexicon.tablespoonCountNounTokens,
			in IngredientLineParserTokens.tablespoonExtraCountNounTokens,
				-> "tbsp"

			in IngredientLineParserTokens.cupExtraCountNounTokens -> "cup"
			"pint", "pints" -> "pint"
			"quart", "quarts" -> "quart"
			"gallon", "gallons" -> "gallon"
			else -> "piece"
		}

	private fun bayLeafName(value: String): String {
		val trimmed = value.trim()
		val compact = trimmed.lowercase().replace(" ", "")
		return when {
			trimmed.equals("bay", ignoreCase = true) -> "bay leaf"
			compact == "bayleaf" || compact == "bayleaves" -> "bay leaf"
			else -> trimmed
		}
	}

	private fun inferCountUnit(parsedName: String, hasQuantity: Boolean): String? {
		val first = normalizeUnit(firstToken(parsedName))
		val lookupTokens = NutritionNameNormalizer.forLookup(parsedName)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		val isGarlicClove = lookupTokens.any { token -> token == "garlic" } &&
			lookupTokens.any { token -> token.startsWith("clove") }
		val garlicOrCloveUnit = when {
			first == "egg" || first == "clove" || first == "piece" -> first
			isGarlicClove || isFreshGarlic(lookupTokens) -> "clove"
			else -> null
		}
		return garlicOrCloveUnit
			?: if (hasQuantity && isPieceCountFood(lookupTokens)) "piece" else null
	}

	private fun isPieceCountFood(lookupTokens: List<String>): Boolean {
		val isCountedPoultryBreastOrThigh =
			lookupTokens.any { token -> token in IngredientLineParserTokens.countablePoultryTokens } &&
				lookupTokens.any { token -> token in IngredientLineParserTokens.bareBreastOrThighTokens }
		return isCountableProduce(lookupTokens) ||
			isCountableMeatName(lookupTokens) ||
			isDefaultablePoultry(lookupTokens) ||
			isCountedPoultryBreastOrThigh ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceProteinTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceCheeseTokens } ||
			(
				lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPieceBreadTokens } &&
					lookupTokens.none { token -> token == "garlic" }
				) ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPiecePepperTokens } ||
			lookupTokens.any { token -> token in IngredientLineParserTokens.defaultPiecePreparedTokens } ||
			lookupTokens.any { token -> token == "chocolate" }
	}

	private fun isCountableProduce(lookupTokens: List<String>): Boolean {
		val isJuiceOrZest = lookupTokens.any { token -> token == "juice" || token == "zest" }
		if (isJuiceOrZest) {
			return false
		}
		val namedProduce = lookupTokens.any { token -> token in IngredientLineParserLexicon.defaultOnePieceTokens }
		val specialProduce = isBellPepper(lookupTokens) ||
			isColoredOrSweetPepper(lookupTokens) ||
			isCornOnTheCob(lookupTokens)
		return namedProduce || specialProduce || isHotDog(lookupTokens)
	}

	private fun isBellPepper(lookupTokens: List<String>): Boolean =
		lookupTokens.any { token -> token == "bell" } &&
			lookupTokens.any { token -> token == "pepper" || token == "peppers" }

	private fun isColoredOrSweetPepper(lookupTokens: List<String>): Boolean {
		val hasPepper = lookupTokens.any { token -> token == "pepper" || token == "peppers" }
		val hasColor = lookupTokens.any { token ->
			token == "red" || token == "green" || token == "yellow" || token == "sweet"
		}
		val isSpicePepper = lookupTokens.any { token ->
			token == "black" || token == "white" || token == "flakes" || token == "flake" || token == "crushed"
		}
		return hasPepper && hasColor && !isSpicePepper
	}

	private fun isCornOnTheCob(lookupTokens: List<String>): Boolean {
		val hasCorn = lookupTokens.any { token ->
			token == "corn" || token == "sweetcorn"
		}
		val hasCob = lookupTokens.any { token -> token == "cob" || token == "cobs" }
		return hasCorn && hasCob
	}

	private val countableMeatAnimalTokens = setOf(
		"beef",
		"duck",
		"ham",
		"lamb",
		"pig",
		"pork",
		"salmon",
		"tuna",
		"turkey",
	)

	private fun isCountableMeatName(lookupTokens: List<String>): Boolean {
		val hasSausage = lookupTokens.any { token ->
			token in IngredientLineParserLexicon.countableSausageTokens
		}
		val hasPoultry = lookupTokens.any { token -> token in IngredientLineParserTokens.countablePoultryTokens }
		val hasNonBareCut = lookupTokens.any { token -> token in IngredientLineParserTokens.extraCountableMeatTokens }
		val hasWholeBird = lookupTokens.any { token -> token in IngredientLineParserTokens.wholeBirdTokens }
		val hasCountedAnimalCut = lookupTokens.any { token ->
			token in IngredientLineParserTokens.extraCountableMeatTokens ||
				token in IngredientLineParserTokens.countableMeatCutTokens
		} && lookupTokens.any { token -> token in countableMeatAnimalTokens }
		val hasNamedMeatCut = lookupTokens.any { token -> token in IngredientLineParserTokens.countableMeatCutTokens }
		val hasHam = lookupTokens.any { token -> token == "ham" }
		return hasSausage ||
			hasHam ||
			(hasPoultry && hasNonBareCut) ||
			(hasPoultry && hasWholeBird) ||
			hasCountedAnimalCut ||
			hasNamedMeatCut
	}

	private fun parsedNameForUnit(unit: String?, rest: String): String {
		if (unit != "clove") {
			return rest
		}
		val trimmed = rest.trim()
		return when {
			trimmed.isEmpty() -> "garlic"
			trimmed.equals("clove", ignoreCase = true) || trimmed.equals("cloves", ignoreCase = true) -> "garlic"
			trimmed.equals("ground", ignoreCase = true) -> "ground clove"
			isGarlicOrCloveSpiceName(trimmed) -> trimmed
			else -> "garlic"
		}
	}

	private fun resolvedParsedName(unit: String?, rest: String, rawText: String): String {
		val fromUnit = parsedNameForUnit(unit = unit, rest = rest)
		if (fromUnit.isNotBlank()) {
			return fromUnit
		}
		return if (unit == null) {
			rawText
		} else {
			""
		}
	}

	private fun isGarlicOrCloveSpiceName(rest: String): Boolean {
		val lookupTokens = NutritionNameNormalizer.forLookup(rest)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
		return lookupTokens.any { token ->
			token == "garlic" ||
				token == "clove" ||
				token == "cloves" ||
				token == "ground"
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

	private const val MIN_INTERNAL_COUNT_NOUN_TOKENS = 3
}

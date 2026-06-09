package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.math.RoundingMode

internal object IngredientLineParser {
	private const val QUANTITY_SCALE = 4

	private val quantityUnitNamePattern = Regex(
		"""
		^\s*
		(?<qty>\d+\s*/\s*\d+|\d+(?:\.\d+)?)
		(?:\s*-\s*(?:\d+\s*/\s*\d+|\d+(?:\.\d+)?))?
		\s*(?<unit>[a-zA-Z][a-zA-Z.\-]*)?
		\s+(?<name>.+)$
		""".trimIndent().replace("\n", ""),
		RegexOption.IGNORE_CASE,
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

		val match = quantityUnitNamePattern.find(rawText) ?: return ParsedIngredientLine(
			rawText = rawText,
			quantity = null,
			unit = null,
			parsedName = rawText,
			isMeasurable = false,
		)

		val quantity = parseQuantity(match.groups["qty"]?.value.orEmpty())
		val unit = normalizeUnit(match.groups["unit"]?.value)
		val parsedName = match.groups["name"]?.value?.trim().orEmpty().ifBlank { rawText }
		val isMeasurable = quantity != null && unit != null && unit in knownUnits

		return ParsedIngredientLine(
			rawText = rawText,
			quantity = quantity,
			unit = unit,
			parsedName = parsedName,
			isMeasurable = isMeasurable,
		)
	}

	private fun parseQuantity(rawQuantity: String): BigDecimal? {
		val trimmed = rawQuantity.trim()
		if (trimmed.isEmpty()) {
			return null
		}
		if (!trimmed.contains('/')) {
			return trimmed.toBigDecimalOrNull()
		}

		val parts = trimmed.split('/').map { it.trim() }
		val numerator = parts.getOrNull(0)?.toBigDecimalOrNull()
		val denominator = parts.getOrNull(1)?.toBigDecimalOrNull()
		val hasValidFraction = parts.size == 2 &&
			numerator != null &&
			denominator != null &&
			denominator.compareTo(BigDecimal.ZERO) != 0
		return if (hasValidFraction) {
			numerator!!.divide(denominator!!, QUANTITY_SCALE, RoundingMode.HALF_UP)
		} else {
			null
		}
	}

	private fun normalizeUnit(rawUnit: String?): String? {
		val unit = rawUnit?.trim()?.lowercase()?.trimEnd('.') ?: return null
		return when (unit) {
			"g", "gram", "grams" -> "g"
			"kg", "kilogram", "kilograms" -> "kg"
			"ml", "milliliter", "millilitre", "milliliters", "millilitres" -> "ml"
			"l", "liter", "litre", "liters", "litres" -> "l"
			"tsp", "teaspoon", "teaspoons" -> "tsp"
			"tbsp", "tablespoon", "tablespoons" -> "tbsp"
			"cup", "cups" -> "cup"
			"oz", "ounce", "ounces" -> "oz"
			"lb", "lbs", "pound", "pounds" -> "lb"
			"egg", "eggs" -> "egg"
			"clove", "cloves" -> "clove"
			"piece", "pieces" -> "piece"
			else -> unit
		}
	}
}

package app.purecipes.backend.feature.nutrition

import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.math.RoundingMode

internal object IngredientLineParser {
	private val quantityUnitNamePattern = Regex(
		"""^\s*(?<qty>\d+(?:\.\d+)?|\d+\s*/\s*\d+)(?:\s*-\s*(?:\d+(?:\.\d+)?|\d+\s*/\s*\d+))?\s*(?<unit>[a-zA-Z][a-zA-Z.\-]*)?\s+(?<name>.+)$""",
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

		val match = quantityUnitNamePattern.find(rawText)
		if (match == null) {
			return ParsedIngredientLine(
				rawText = rawText,
				quantity = null,
				unit = null,
				parsedName = rawText,
				isMeasurable = false,
			)
		}

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
		if (trimmed.contains('/')) {
			val parts = trimmed.split('/').map { it.trim() }
			if (parts.size != 2) {
				return null
			}
			val numerator = parts[0].toBigDecimalOrNull() ?: return null
			val denominator = parts[1].toBigDecimalOrNull() ?: return null
			if (denominator.compareTo(BigDecimal.ZERO) == 0) {
				return null
			}
			return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP)
		}
		return trimmed.toBigDecimalOrNull()
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

	private const val SCALE = 4
}

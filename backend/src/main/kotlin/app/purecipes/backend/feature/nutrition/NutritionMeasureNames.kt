package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import java.math.RoundingMode

internal object NutritionMeasureNames {

	private const val UNDETERMINED = "undetermined"
	private const val GRAMS_SCALE = 4
	private val parentheticalPattern = Regex("""\([^)]*\)""")
	private val householdUnits = setOf(
		"ml",
		"l",
		"tsp",
		"tbsp",
		"cup",
		"egg",
		"clove",
		"piece",
	)

	fun resolveImportedName(measureUnitName: String?, modifier: String?): String? =
		householdUnit(measureUnitName) ?: householdUnitFromModifier(modifier)

	fun gramsPerSingleMeasure(gramWeight: BigDecimal, amount: BigDecimal?): BigDecimal {
		val divisor = amount?.takeIf { value -> value.signum() > 0 } ?: BigDecimal.ONE
		return gramWeight.divide(divisor, GRAMS_SCALE, RoundingMode.HALF_UP)
	}

	private fun householdUnitFromModifier(modifier: String?): String? {
		if (modifier.isNullOrBlank()) {
			return null
		}
		val withoutParens = parentheticalPattern.replace(modifier, " ")
		val primary = withoutParens.substringBefore(',').trim()
		return householdUnit(primary) ?: householdUnit(primary.substringBefore(' '))
	}

	private fun householdUnit(rawName: String?): String? {
		val normalized = canonicalize(rawName) ?: return null
		return normalized.takeIf { unit -> unit in householdUnits }
	}

	private fun canonicalize(rawName: String?): String? {
		val unit = rawName
			?.trim()
			?.lowercase()
			?.trimEnd('.', ';')
			?.takeIf { value -> value.isNotEmpty() && value != UNDETERMINED }
			?: return null
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
			"piece", "pieces", "each", "item", "items",
			"fruit", "fruits", "whole", "small", "medium", "large",
			-> "piece"
			else -> unit
		}
	}
}

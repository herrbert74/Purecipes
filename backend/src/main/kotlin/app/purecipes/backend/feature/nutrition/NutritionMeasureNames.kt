package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal
import java.math.RoundingMode

internal object NutritionMeasureNames {

	private const val UNDETERMINED = "undetermined"
	private const val GRAMS_SCALE = 4
	private const val HEAD_MEDIUM_PREFERENCE = 100
	private const val HEAD_PREFERENCE = 80
	private const val EAR_PREFERENCE = 70
	private const val STALK_PREFERENCE = 60
	private const val PEPPER_PREFERENCE = 55
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

	fun pieceImportPreference(modifier: String?): Int {
		val raw = modifier?.trim()?.lowercase().orEmpty()
		if (raw.isEmpty()) {
			return 0
		}
		val withoutParens = parentheticalPattern.replace(raw, " ")
		val hasMedium = withoutParens.contains("medium")
		val first = withoutParens.substringBefore(',').trim().substringBefore(' ')
		return when (first) {
			"head" -> if (hasMedium) HEAD_MEDIUM_PREFERENCE else HEAD_PREFERENCE
			"ear" -> if (hasMedium) HEAD_MEDIUM_PREFERENCE else EAR_PREFERENCE
			"stalk" -> if (hasMedium) HEAD_MEDIUM_PREFERENCE else STALK_PREFERENCE
			"pepper" -> PEPPER_PREFERENCE
			else -> 0
		}
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
			"head", "heads", "ear", "ears", "stalk", "stalks",
			"avocado", "avocados", "cucumber", "cucumbers",
			"eggplant", "eggplants", "leek", "leeks",
			"onion", "onions", "pepper",
			"potato", "potatoes", "sweetpotato",
				-> "piece"

			else -> unit
		}
	}
}

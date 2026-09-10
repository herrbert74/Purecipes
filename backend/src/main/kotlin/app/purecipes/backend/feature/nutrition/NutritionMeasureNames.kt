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
	private val brandedHouseholdPattern = Regex("""^\s*(\d+(?:\.\d+)?)\s+([A-Za-z]+)\b""")
	private val surveyPortionPattern = Regex("""^\s*(\d+(?:\.\d+)?)\s+(.+)$""")
	private val flOzMl = BigDecimal("29.5735")
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

	fun brandedHouseholdPortion(
		servingSize: BigDecimal?,
		servingSizeUnit: String?,
		householdServingFullText: String?,
	): FdcFoodPortion? {
		val grams = brandedServingGrams(servingSize, servingSizeUnit)
		val householdMatch = brandedHouseholdMatch(householdServingFullText)
		return if (grams == null || householdMatch == null) {
			null
		} else {
			FdcFoodPortion(
				measureName = householdMatch.measureName,
				gramsPerMeasure = gramsPerSingleMeasure(grams, householdMatch.amount),
			)
		}
	}

	private fun brandedServingGrams(servingSize: BigDecimal?, servingSizeUnit: String?): BigDecimal? {
		val hasPositiveGrams = servingSize != null &&
			servingSize.signum() > 0 &&
			canonicalize(servingSizeUnit) == "g"
		return if (hasPositiveGrams) {
			servingSize
		} else {
			null
		}
	}

	private fun brandedHouseholdMatch(householdServingFullText: String?): BrandedHouseholdMatch? {
		val household = householdServingFullText?.trim()?.takeIf { value -> value.isNotEmpty() }
		val match = household?.let { text -> brandedHouseholdPattern.find(text) }
		val amount = match?.groupValues?.get(1)?.toBigDecimalOrNull()?.takeIf { value -> value.signum() > 0 }
		val measureName = match?.groupValues?.get(2)?.let { unit -> resolveImportedName(unit, null) }
		return if (amount == null || measureName == null) {
			null
		} else {
			BrandedHouseholdMatch(amount = amount, measureName = measureName)
		}
	}

	fun surveyPortionFromDescription(
		portionDescription: String?,
		gramWeight: BigDecimal?,
	): FdcFoodPortion? {
		val parsed = parseSurveyPortion(portionDescription, gramWeight) ?: return null
		val gramsPerMeasure = if (parsed.fluidOunce) {
			gramWeight!!.divide(parsed.amount.multiply(flOzMl), GRAMS_SCALE, RoundingMode.HALF_UP)
		} else {
			gramsPerSingleMeasure(gramWeight!!, parsed.amount)
		}
		return FdcFoodPortion(measureName = parsed.measureName, gramsPerMeasure = gramsPerMeasure)
	}

	private fun parseSurveyPortion(
		portionDescription: String?,
		gramWeight: BigDecimal?,
	): SurveyPortionParse? {
		val hasWeight = gramWeight != null && gramWeight.signum() > 0
		val raw = portionDescription?.trim()?.takeIf { value -> value.isNotEmpty() }
		if (!hasWeight || raw == null || isIgnoredSurveyPortion(raw)) {
			return null
		}
		val match = surveyPortionPattern.find(raw)
		val amount = match?.groupValues?.get(1)?.toBigDecimalOrNull()?.takeIf { value -> value.signum() > 0 }
		val measureText = match?.groupValues?.get(2)?.let(::normalizeSurveyMeasureText)
		val fluidOunce = measureText != null && isFluidOunceMeasure(measureText)
		val measureName = surveyMeasureName(measureText, fluidOunce)
		return if (amount == null || measureName == null) {
			null
		} else {
			SurveyPortionParse(
				amount = amount,
				measureName = measureName,
				fluidOunce = fluidOunce,
			)
		}
	}

	private fun isIgnoredSurveyPortion(raw: String): Boolean =
		raw.startsWith("Quantity not specified", ignoreCase = true) ||
			raw.startsWith("Guideline", ignoreCase = true)

	private fun normalizeSurveyMeasureText(text: String): String =
		parentheticalPattern.replace(text, " ").substringBefore(',').trim().lowercase()

	private fun isFluidOunceMeasure(measureText: String): Boolean =
		measureText.startsWith("fl oz") ||
			measureText.startsWith("fluid oz") ||
			measureText.startsWith("fluid ounce")

	private fun surveyMeasureName(measureText: String?, fluidOunce: Boolean): String? =
		when {
			measureText == null -> null
			fluidOunce -> "ml"
			else -> resolveImportedName(measureText.substringBefore(' '), null)
				?: resolveImportedName(measureText, null)
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

private data class BrandedHouseholdMatch(
	val amount: BigDecimal,
	val measureName: String,
)

private data class SurveyPortionParse(
	val amount: BigDecimal,
	val measureName: String,
	val fluidOunce: Boolean,
)

package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.feature.measurement.domain.model.ProcessedRecipeDetails
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
import dev.zacsweers.metro.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

@Inject
class ProcessRecipeDetailsForMeasurementPreferencesUseCase {

	operator fun invoke(
		recipe: RecipeDetails,
		preferences: MeasurementPreferences,
	): ProcessedRecipeDetails {
		val originalMeasurementSystem = recipe.effectiveMeasurementSystem()
		val preferredSystem = preferences.preferredSystem
		val isMismatch =
			originalMeasurementSystem != null &&
				originalMeasurementSystem != MeasurementSystem.MIXED &&
				originalMeasurementSystem != preferredSystem

		if (!isMismatch) {
			return ProcessedRecipeDetails(
				recipe = recipe,
				originalMeasurementSystem = originalMeasurementSystem,
				isConverted = false,
				shouldShowMismatchNotification = false,
			)
		}

		if (preferences.formatHandling == RecipeFormatHandling.CONVERT_TO_PREFERRED) {
			return ProcessedRecipeDetails(
				recipe = recipe.convertTo(preferredSystem),
				originalMeasurementSystem = originalMeasurementSystem,
				isConverted = true,
				shouldShowMismatchNotification = false,
			)
		}

		return ProcessedRecipeDetails(
			recipe = recipe,
			originalMeasurementSystem = originalMeasurementSystem,
			isConverted = false,
			shouldShowMismatchNotification =
				preferences.formatHandling == RecipeFormatHandling.KEEP_AS_IS &&
					recipe.id !in preferences.notificationSeenRecipeIds,
		)
	}

	private fun RecipeDetails.effectiveMeasurementSystem(): MeasurementSystem? {
		return measurementSystem ?: detectMeasurementSystem(ingredientGroups)
	}

	private fun detectMeasurementSystem(ingredientGroups: List<IngredientGroup>): MeasurementSystem? {
		var imperialHits = 0
		var metricHits = 0
		ingredientGroups.asSequence()
			.flatMap { it.ingredients.asSequence() }
			.forEach { ingredient ->
				ingredientConversionRegex.findAll(ingredient).forEach { match ->
					val unitInfo = unitInfoFor(match.groups[SECOND_CAPTURE_GROUP]?.value ?: return@forEach)
						?: return@forEach
					when (unitInfo.system) {
						MeasurementSystem.IMPERIAL -> imperialHits += 1
						MeasurementSystem.METRIC -> metricHits += 1
						MeasurementSystem.MIXED -> Unit
					}
				}
			}
		return when {
			imperialHits == 0 && metricHits == 0 -> null
			imperialHits > 0 && metricHits > 0 -> MeasurementSystem.MIXED
			imperialHits > 0 -> MeasurementSystem.IMPERIAL
			else -> MeasurementSystem.METRIC
		}
	}

	private fun RecipeDetails.convertTo(preferredSystem: MeasurementSystem): RecipeDetails {
		return copy(
			ingredientGroups = ingredientGroups.map { group ->
				IngredientGroup(
					name = group.name,
					ingredients = group.ingredients.map { ingredient ->
						convertIngredientLine(ingredient, preferredSystem)
					},
				)
			},
			steps = steps.map { step -> convertTemperatureReferences(step, preferredSystem) },
			measurementSystem = preferredSystem,
		)
	}

	private fun convertIngredientLine(
		line: String,
		targetSystem: MeasurementSystem,
	): String {
		var convertedLine = line
		ingredientConversionRegex.findAll(line).toList().asReversed().forEach { match ->
			val parsedValue =
				parseMeasurementValue(match.groups[FIRST_CAPTURE_GROUP]?.value ?: return@forEach)
					?: return@forEach
			val unitInfo = unitInfoFor(match.groups[SECOND_CAPTURE_GROUP]?.value ?: return@forEach)
				?: return@forEach
			if (unitInfo.system == targetSystem) return@forEach
			val replacement = convertMeasurement(parsedValue, unitInfo, targetSystem) ?: return@forEach
			convertedLine = convertedLine.replaceRange(match.range, replacement)
		}
		return convertedLine
	}

	private fun convertTemperatureReferences(
		text: String,
		targetSystem: MeasurementSystem,
	): String {
		var convertedText = text
		temperatureRegex.findAll(text).toList().asReversed().forEach { match ->
			val rawValue = match.groups[FIRST_CAPTURE_GROUP]?.value?.toDoubleOrNull() ?: return@forEach
			val rawUnit = match.groups[SECOND_CAPTURE_GROUP]?.value ?: return@forEach
			val sourceSystem = when {
				rawUnit.startsWith(FAHRENHEIT_UNIT, ignoreCase = true) -> MeasurementSystem.IMPERIAL
				rawUnit.startsWith(CELSIUS_UNIT, ignoreCase = true) -> MeasurementSystem.METRIC
				else -> return@forEach
			}
			if (sourceSystem == targetSystem) return@forEach
			val convertedValue = when (targetSystem) {
				MeasurementSystem.IMPERIAL -> imperialTemperatureFromCelsius(rawValue)
				MeasurementSystem.METRIC -> metricTemperatureFromFahrenheit(rawValue)
				MeasurementSystem.MIXED -> rawValue
			}
			val convertedUnit = when (targetSystem) {
				MeasurementSystem.IMPERIAL -> IMPERIAL_TEMPERATURE_LABEL
				MeasurementSystem.METRIC -> METRIC_TEMPERATURE_LABEL
				else -> rawUnit
			}
			convertedText = convertedText.replaceRange(
				match.range,
				"${convertedValue.formatMetricNumber()}°$convertedUnit",
			)
		}
		return convertedText
	}

	private fun convertMeasurement(
		value: Double,
		unitInfo: UnitInfo,
		targetSystem: MeasurementSystem,
	): String? {
		if (targetSystem == MeasurementSystem.MIXED) return null
		val baseValue = value * unitInfo.toBaseFactor
		return when (unitInfo.dimension) {
			MeasurementDimension.WEIGHT -> if (targetSystem == MeasurementSystem.METRIC) {
				formatMetricWeight(baseValue)
			} else {
				formatImperialWeight(baseValue)
			}

			MeasurementDimension.VOLUME -> if (targetSystem == MeasurementSystem.METRIC) {
				formatMetricVolume(baseValue)
			} else {
				formatImperialVolume(baseValue)
			}
		}
	}

	private fun formatMetricWeight(grams: Double): String {
		return if (grams >= GRAMS_PER_KILOGRAM) {
			"${(grams / GRAMS_PER_KILOGRAM).roundToSingleDecimal().formatMetricNumber()} kg"
		} else {
			"${grams.roundToNearestWhole().formatMetricNumber()} g"
		}
	}

	private fun formatImperialWeight(grams: Double): String {
		return if (grams >= GRAMS_PER_POUND) {
			"${(grams / GRAMS_PER_POUND).roundToQuarter().formatImperialNumber()} lb"
		} else {
			"${(grams / GRAMS_PER_OUNCE).roundToQuarter().formatImperialNumber()} oz"
		}
	}

	private fun formatMetricVolume(milliliters: Double): String {
		return if (milliliters >= MILLILITERS_PER_LITER) {
			"${(milliliters / MILLILITERS_PER_LITER).roundToSingleDecimal().formatMetricNumber()} L"
		} else {
			"${milliliters.roundToNearestWhole().formatMetricNumber()} mL"
		}
	}

	private fun formatImperialVolume(milliliters: Double): String {
		val cups = milliliters / MILLILITERS_PER_CUP
		if (cups >= MINIMUM_CUP_COUNT) {
			return "${cups.roundToQuarter().formatImperialNumber()} cup"
		}
		val tablespoons = milliliters / MILLILITERS_PER_TABLESPOON
		if (tablespoons >= MINIMUM_TABLESPOON_COUNT) {
			return "${tablespoons.roundToQuarter().formatImperialNumber()} tbsp"
		}
		return "${(milliliters / MILLILITERS_PER_TEASPOON).roundToQuarter().formatImperialNumber()} tsp"
	}

	private fun parseMeasurementValue(rawValue: String): Double? {
		val normalized = rawValue.trim()
		return when {
			'/' in normalized && ' ' in normalized -> {
				val parts = normalized.split(' ', limit = FRACTION_PART_COUNT)
				parts.firstOrNull()?.toDoubleOrNull()?.plus(parseFraction(parts.getOrNull(SECOND_INDEX)).orZero())
			}

			'/' in normalized -> parseFraction(normalized)
			else -> normalized.toDoubleOrNull()
		}
	}

	private fun parseFraction(rawFraction: String?): Double? {
		val values = rawFraction?.split('/', limit = FRACTION_PART_COUNT)
		val numerator = values?.getOrNull(FIRST_INDEX)?.toDoubleOrNull()
		val denominator = values?.getOrNull(SECOND_INDEX)?.toDoubleOrNull()?.takeIf { it != ZERO_DOUBLE }
		return if (numerator != null && denominator != null) numerator / denominator else null
	}

	private fun unitInfoFor(rawUnit: String): UnitInfo? {
		val normalized = rawUnit.lowercase().removeSuffix(PERIOD_SUFFIX)
		return unitInfos.firstOrNull { normalized in it.aliases }
	}

	private fun imperialTemperatureFromCelsius(rawValue: Double): Double {
		return ((rawValue * FAHRENHEIT_NUMERATOR / CELSIUS_DENOMINATOR) + FAHRENHEIT_OFFSET).roundToNearestFive()
	}

	private fun metricTemperatureFromFahrenheit(rawValue: Double): Double {
		return ((rawValue - FAHRENHEIT_OFFSET) * CELSIUS_DENOMINATOR / FAHRENHEIT_NUMERATOR).roundToNearestFive()
	}

	private fun Double.roundToQuarter(): Double = (this * QUARTER_STEP).roundToInt() / QUARTER_STEP

	private fun Double.roundToSingleDecimal(): Double = (this * TEN_SCALE).roundToInt() / TEN_SCALE

	private fun Double.roundToNearestWhole(): Double = roundToInt().toDouble()

	private fun Double.roundToNearestFive(): Double = (this / FIVE_SCALE).roundToInt() * FIVE_SCALE

	private fun Double.formatMetricNumber(): String {
		val rounded = if (abs(this - roundToInt().toDouble()) < ROUNDING_THRESHOLD) {
			roundToInt().toDouble()
		} else {
			this
		}
		return if (abs(rounded - rounded.toInt()) < ROUNDING_THRESHOLD) {
			rounded.toInt().toString()
		} else {
			rounded.toString().removeSuffix(ZERO_SUFFIX).removeSuffix(PERIOD_SUFFIX)
		}
	}

	private fun Double.formatImperialNumber(): String {
		val rounded = roundToQuarter()
		val whole = rounded.toInt()
		val remainder = ((rounded - whole) * QUARTER_STEP).roundToInt()
		val fraction = when (remainder) {
			ZERO_FRACTION -> EMPTY_VALUE
			ONE_QUARTER -> QUARTER_LABEL
			ONE_HALF -> HALF_LABEL
			THREE_QUARTERS -> THREE_QUARTERS_LABEL
			else -> EMPTY_VALUE
		}
		return when {
			whole == ZERO_FRACTION && fraction.isNotEmpty() -> fraction
			fraction.isEmpty() -> whole.toString()
			else -> "$whole $fraction"
		}
	}

	private fun Double?.orZero(): Double = this ?: ZERO_DOUBLE

	private data class UnitInfo(
		val aliases: Set<String>,
		val system: MeasurementSystem,
		val dimension: MeasurementDimension,
		val toBaseFactor: Double,
	)

	private enum class MeasurementDimension {
		WEIGHT,
		VOLUME,
	}

	private companion object {
		const val CELSIUS_DENOMINATOR = 5.0
		const val FAHRENHEIT_NUMERATOR = 9.0
		const val FAHRENHEIT_OFFSET = 32.0
		const val FIVE_SCALE = 5.0
		const val FIRST_CAPTURE_GROUP = 1
		const val SECOND_CAPTURE_GROUP = 2
		const val FIRST_INDEX = 0
		const val SECOND_INDEX = 1
		const val FRACTION_PART_COUNT = 2
		const val GRAMS_PER_KILOGRAM = 1000.0
		const val GRAMS_PER_OUNCE = 28.3495
		const val GRAMS_PER_POUND = 453.592
		const val MINIMUM_CUP_COUNT = 0.25
		const val MINIMUM_TABLESPOON_COUNT = 1.0
		const val MILLILITERS_PER_LITER = 1000.0
		const val MILLILITERS_PER_CUP = 236.588
		const val MILLILITERS_PER_TABLESPOON = 14.7868
		const val MILLILITERS_PER_TEASPOON = 4.92892
		const val ONE_DOUBLE = 1.0
		const val QUARTER_STEP = 4.0
		const val ROUNDING_THRESHOLD = 0.05
		const val TEN_SCALE = 10.0
		const val ZERO_DOUBLE = 0.0
		const val ZERO_FRACTION = 0
		const val ONE_QUARTER = 1
		const val ONE_HALF = 2
		const val THREE_QUARTERS = 3
		const val CELSIUS_UNIT = "c"
		const val FAHRENHEIT_UNIT = "f"
		const val EMPTY_VALUE = ""
		const val HALF_LABEL = "1/2"
		const val IMPERIAL_TEMPERATURE_LABEL = "F"
		const val METRIC_TEMPERATURE_LABEL = "C"
		const val PERIOD_SUFFIX = "."
		const val QUARTER_LABEL = "1/4"
		const val THREE_QUARTERS_LABEL = "3/4"
		const val ZERO_SUFFIX = "0"

		val ingredientConversionRegex = Regex(
			pattern =
				"""(?<!\p{L})(\d+(?:\.\d+)?|\d+\s+\d+/\d+|\d+/\d+)\s*""" +
					"""(cups?|cup|tablespoons?|tablespoon|tbsp\.?|teaspoons?|teaspoon|tsp\.?|""" +
					"""ounces?|ounce|oz\.?|pounds?|pound|lbs?\.?|kilograms?|kilogram|kg|""" +
					"""grams?|gram|g|milliliters?|milliliter|ml|liters?|liter|l)\b""",
			options = setOf(RegexOption.IGNORE_CASE),
		)

		val temperatureRegex = Regex(
			pattern = """(\d+(?:\.\d+)?)\s*(?:°\s*)?(F|C|fahrenheit|celsius)\b""",
			options = setOf(RegexOption.IGNORE_CASE),
		)

		val unitInfos = listOf(
			UnitInfo(
				aliases = setOf("cup", "cups"),
				system = MeasurementSystem.IMPERIAL,
				dimension = MeasurementDimension.VOLUME,
				toBaseFactor = MILLILITERS_PER_CUP,
			),
			UnitInfo(
				aliases = setOf("tablespoon", "tablespoons", "tbsp"),
				system = MeasurementSystem.IMPERIAL,
				dimension = MeasurementDimension.VOLUME,
				toBaseFactor = MILLILITERS_PER_TABLESPOON,
			),
			UnitInfo(
				aliases = setOf("teaspoon", "teaspoons", "tsp"),
				system = MeasurementSystem.IMPERIAL,
				dimension = MeasurementDimension.VOLUME,
				toBaseFactor = MILLILITERS_PER_TEASPOON,
			),
			UnitInfo(
				aliases = setOf("ounce", "ounces", "oz"),
				system = MeasurementSystem.IMPERIAL,
				dimension = MeasurementDimension.WEIGHT,
				toBaseFactor = GRAMS_PER_OUNCE,
			),
			UnitInfo(
				aliases = setOf("pound", "pounds", "lb", "lbs"),
				system = MeasurementSystem.IMPERIAL,
				dimension = MeasurementDimension.WEIGHT,
				toBaseFactor = GRAMS_PER_POUND,
			),
			UnitInfo(
				aliases = setOf("kg", "kilogram", "kilograms"),
				system = MeasurementSystem.METRIC,
				dimension = MeasurementDimension.WEIGHT,
				toBaseFactor = GRAMS_PER_KILOGRAM,
			),
			UnitInfo(
				aliases = setOf("g", "gram", "grams"),
				system = MeasurementSystem.METRIC,
				dimension = MeasurementDimension.WEIGHT,
				toBaseFactor = ONE_DOUBLE,
			),
			UnitInfo(
				aliases = setOf("l", "liter", "liters"),
				system = MeasurementSystem.METRIC,
				dimension = MeasurementDimension.VOLUME,
				toBaseFactor = MILLILITERS_PER_LITER,
			),
			UnitInfo(
				aliases = setOf("ml", "milliliter", "milliliters"),
				system = MeasurementSystem.METRIC,
				dimension = MeasurementDimension.VOLUME,
				toBaseFactor = ONE_DOUBLE,
			),
		)
	}
}

package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal object IngredientGramWeightResolver {

	private val gramsPerOunce = BigDecimal("28.3495")
	private val gramsPerPound = BigDecimal("453.592")
	private val gramsPerKilogram = BigDecimal("1000")
	private val millilitersPerLiter = BigDecimal("1000")
	private val defaultGramsPerMilliliter = BigDecimal.ONE
	private val defaultGramsPerTeaspoon = BigDecimal("5")
	private val defaultGramsPerTablespoon = BigDecimal("15")
	private val defaultGramsPerCup = BigDecimal("240")

	fun resolveGrams(
		quantity: BigDecimal,
		unit: String,
		foodMeasures: Map<String, BigDecimal>,
	): ResolvedIngredientGrams? =
		when (unit) {
			"g" -> ResolvedIngredientGrams(grams = quantity, source = GramWeightSource.MASS)
			"kg" -> ResolvedIngredientGrams(
				grams = quantity.multiply(gramsPerKilogram),
				source = GramWeightSource.MASS,
			)

			"oz" -> ResolvedIngredientGrams(
				grams = quantity.multiply(gramsPerOunce),
				source = GramWeightSource.MASS,
			)

			"lb" -> ResolvedIngredientGrams(
				grams = quantity.multiply(gramsPerPound),
				source = GramWeightSource.MASS,
			)

			else -> gramsPerHouseholdUnit(unit, foodMeasures)?.let { resolved ->
				ResolvedIngredientGrams(
					grams = quantity.multiply(resolved.grams),
					source = resolved.source,
				)
			}
		}

	private fun gramsPerHouseholdUnit(
		unit: String,
		foodMeasures: Map<String, BigDecimal>,
	): ResolvedIngredientGrams? =
		foodMeasures[unit]?.let { grams ->
			ResolvedIngredientGrams(grams = grams, source = GramWeightSource.MEASURE)
		} ?: defaultGramsPerHouseholdUnit(unit, foodMeasures)?.let { grams ->
			ResolvedIngredientGrams(grams = grams, source = GramWeightSource.DENSITY)
		}

	private fun defaultGramsPerHouseholdUnit(
		unit: String,
		foodMeasures: Map<String, BigDecimal>,
	): BigDecimal? =
		when (unit) {
			"ml" -> defaultGramsPerMilliliter
			"l" -> foodMeasures["ml"]?.multiply(millilitersPerLiter) ?: millilitersPerLiter
			"tsp" -> defaultGramsPerTeaspoon
			"tbsp" -> defaultGramsPerTablespoon
			"cup" -> defaultGramsPerCup
			else -> null
		}
}

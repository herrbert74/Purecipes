package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal object IngredientGramWeightResolver {
	private val gramsPerOunce = BigDecimal("28.3495")
	private val gramsPerPound = BigDecimal("453.592")
	private val gramsPerKilogram = BigDecimal("1000")
	private val millilitersPerLiter = BigDecimal("1000")

	fun resolveGrams(
		quantity: BigDecimal,
		unit: String,
		foodMeasures: Map<String, BigDecimal>,
	): BigDecimal? =
		when (unit) {
			"g" -> quantity
			"kg" -> quantity.multiply(gramsPerKilogram)
			"oz" -> quantity.multiply(gramsPerOunce)
			"lb" -> quantity.multiply(gramsPerPound)
			"ml" -> foodMeasures["ml"]?.multiply(quantity)
			"l" -> foodMeasures["l"]?.multiply(quantity)
				?: foodMeasures["ml"]?.multiply(quantity.multiply(millilitersPerLiter))
			else -> foodMeasures[unit]?.multiply(quantity)
		}
}

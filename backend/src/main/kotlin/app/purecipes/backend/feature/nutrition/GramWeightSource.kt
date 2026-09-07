package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class ResolvedIngredientGrams(
	val grams: BigDecimal,
	val source: String,
)

internal object GramWeightSource {

	const val MASS = "mass"
	const val MEASURE = "measure"
	const val DENSITY = "density"
}

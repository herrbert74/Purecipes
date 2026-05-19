package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class SupplementalMeasure(
	val measureName: String,
	val gramsPerMeasure: BigDecimal,
)

internal object NutritionSupplementalMeasures {
	private const val FLOUR_FDC_ID = 789951L
	private const val YELLOW_ONION_FDC_ID = 790646L
	private const val GARLIC_FDC_ID = 1104647L

	val measuresByFdcId: Map<Long, List<SupplementalMeasure>> = mapOf(
		FLOUR_FDC_ID to listOf(
			SupplementalMeasure(measureName = "cup", gramsPerMeasure = BigDecimal("120")),
			SupplementalMeasure(measureName = "tbsp", gramsPerMeasure = BigDecimal("7.5")),
		),
		YELLOW_ONION_FDC_ID to listOf(
			SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal("110")),
		),
		GARLIC_FDC_ID to listOf(
			SupplementalMeasure(measureName = "clove", gramsPerMeasure = BigDecimal("3")),
		),
	)
}

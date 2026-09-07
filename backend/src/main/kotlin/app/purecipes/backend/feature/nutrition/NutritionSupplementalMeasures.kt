package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class SupplementalMeasure(
	val measureName: String,
	val gramsPerMeasure: BigDecimal,
)

internal object NutritionSupplementalMeasures {

	private const val BROCCOLI_FDC_ID = 170379L
	private const val CABBAGE_FDC_ID = 169975L
	private const val CAULIFLOWER_FDC_ID = 169986L
	private const val CELERY_FDC_ID = 169988L
	private const val CORN_FDC_ID = 169998L
	private const val FLOUR_FDC_ID = 789951L
	private const val GARLIC_FDC_ID = 1104647L
	private const val RED_CABBAGE_FDC_ID = 169977L
	private const val SHALLOT_FDC_ID = 170499L
	private const val YELLOW_ONION_FDC_ID = 790646L
	private const val YELLOW_PEPPER_FDC_ID = 169383L

	private const val BROCCOLI_DESCRIPTION = "Broccoli, raw"
	private const val CABBAGE_DESCRIPTION = "Cabbage, raw"
	private const val CAULIFLOWER_DESCRIPTION = "Cauliflower, raw"
	private const val CELERY_DESCRIPTION = "Celery, raw"
	private const val CORN_DESCRIPTION = "Corn, sweet, yellow, raw"
	private const val RED_CABBAGE_DESCRIPTION = "Cabbage, red, raw"
	private const val SHALLOT_DESCRIPTION = "Shallots, raw"
	private const val YELLOW_PEPPER_DESCRIPTION = "Peppers, sweet, yellow, raw"

	private val pieceMeasuresByDisplayName: Map<String, List<SupplementalMeasure>> = mapOf(
		BROCCOLI_DESCRIPTION to pieceGrams("151"),
		CABBAGE_DESCRIPTION to pieceGrams("908"),
		CAULIFLOWER_DESCRIPTION to pieceGrams("588"),
		CELERY_DESCRIPTION to pieceGrams("40"),
		CORN_DESCRIPTION to pieceGrams("102"),
		RED_CABBAGE_DESCRIPTION to pieceGrams("839"),
		SHALLOT_DESCRIPTION to pieceGrams("30"),
		YELLOW_PEPPER_DESCRIPTION to pieceGrams("186"),
	)

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
		BROCCOLI_FDC_ID to pieceGrams("151"),
		CABBAGE_FDC_ID to pieceGrams("908"),
		CAULIFLOWER_FDC_ID to pieceGrams("588"),
		CELERY_FDC_ID to pieceGrams("40"),
		CORN_FDC_ID to pieceGrams("102"),
		RED_CABBAGE_FDC_ID to pieceGrams("839"),
		SHALLOT_FDC_ID to pieceGrams("30"),
		YELLOW_PEPPER_FDC_ID to pieceGrams("186"),
	)

	fun overlayMeasures(
		foods: Collection<NutritionFoodRecord>,
		storedMeasures: Map<Int, Map<String, BigDecimal>>,
	): Map<Int, Map<String, BigDecimal>> {
		val result = storedMeasures.mapValues { entry -> entry.value.toMutableMap() }.toMutableMap()
		foods.forEach { food ->
			pieceMeasuresByDisplayName[food.displayName].orEmpty().forEach { supplemental ->
				val measures = result.getOrPut(food.id) { mutableMapOf() }
				if (supplemental.measureName !in measures) {
					measures[supplemental.measureName] = supplemental.gramsPerMeasure
				}
			}
		}
		return result
	}

	private fun pieceGrams(grams: String): List<SupplementalMeasure> =
		listOf(SupplementalMeasure(measureName = "piece", gramsPerMeasure = BigDecimal(grams)))
}

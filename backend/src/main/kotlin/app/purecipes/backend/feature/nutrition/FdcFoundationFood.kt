package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal const val FDC_FOUNDATION_SOURCE_NAME = "fdc_foundation"
internal const val FDC_SR_LEGACY_SOURCE_NAME = "fdc_sr_legacy"

internal enum class FdcFoodDataset(
	val jsonRootKey: String,
	val sourceName: String,
	val sourceMetadata: String,
) {
	FOUNDATION(
		jsonRootKey = "FoundationFoods",
		sourceName = FDC_FOUNDATION_SOURCE_NAME,
		sourceMetadata = """{"dataType":"Foundation"}""",
	),
	SR_LEGACY(
		jsonRootKey = "SRLegacyFoods",
		sourceName = FDC_SR_LEGACY_SOURCE_NAME,
		sourceMetadata = """{"dataType":"SR Legacy"}""",
	),
}

internal data class FdcNutrientAmount(
	val nutrientId: Int,
	val amount: BigDecimal,
)

internal data class FdcFoodPortion(
	val measureName: String,
	val gramsPerMeasure: BigDecimal,
)

internal data class FdcFoundationFood(
	val sourceName: String,
	val fdcId: Long,
	val description: String,
	val nutrients: List<FdcNutrientAmount>,
	val portions: List<FdcFoodPortion>,
) {
	val normalizedDescription: String = NutritionNameNormalizer.normalize(description)

	fun nutrientsPer100g(): FdcNutrientsPer100g? {
		val byId = nutrients.associate { it.nutrientId to it.amount }
		val calories = byId[FdcNutrientIds.ENERGY_KCAL] ?: return null
		return FdcNutrientsPer100g(
			calories = calories,
			protein = byId[FdcNutrientIds.PROTEIN],
			carbohydrates = byId[FdcNutrientIds.CARBOHYDRATES],
			fat = byId[FdcNutrientIds.FAT],
			fiber = byId[FdcNutrientIds.FIBER],
			sugar = byId[FdcNutrientIds.SUGAR],
			sodium = byId[FdcNutrientIds.SODIUM],
		)
	}
}

internal data class FdcNutrientsPer100g(
	val calories: BigDecimal,
	val protein: BigDecimal?,
	val carbohydrates: BigDecimal?,
	val fat: BigDecimal?,
	val fiber: BigDecimal?,
	val sugar: BigDecimal?,
	val sodium: BigDecimal?,
)

internal data class FdcFoodDataParseResult(
	val dataset: FdcFoodDataset,
	val foods: List<FdcFoundationFood>,
)

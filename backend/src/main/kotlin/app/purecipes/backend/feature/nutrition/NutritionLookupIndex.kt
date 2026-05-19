package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class NutritionFoodRecord(
	val id: Int,
	val displayName: String,
	val normalizedName: String,
	val nutrients: FdcNutrientsPer100g,
)

internal data class NutritionFoodMatch(
	val foodId: Int,
	val matchSource: String,
	val confidence: BigDecimal,
)

internal class NutritionLookupIndex(
	private val foodById: Map<Int, NutritionFoodRecord>,
	private val foodIdByNormalizedAlias: Map<String, Int>,
	private val measuresByFoodId: Map<Int, Map<String, BigDecimal>>,
) {
	fun findFood(parsedName: String): NutritionFoodMatch? {
		val normalizedName = NutritionNameNormalizer.normalize(parsedName)
		if (normalizedName.isBlank()) {
			return null
		}

		return matchAlias(normalizedName)
			?: matchExactName(normalizedName)
			?: matchBestPrefix(normalizedName)
	}

	fun food(foodId: Int): NutritionFoodRecord? = foodById[foodId]

	fun measuresForFood(foodId: Int): Map<String, BigDecimal> = measuresByFoodId[foodId].orEmpty()

	private fun matchAlias(normalizedName: String): NutritionFoodMatch? {
		val foodId = foodIdByNormalizedAlias[normalizedName] ?: return null
		return NutritionFoodMatch(
			foodId = foodId,
			matchSource = MATCH_SOURCE_ALIAS,
			confidence = ALIAS_MATCH_CONFIDENCE,
		)
	}

	private fun matchExactName(normalizedName: String): NutritionFoodMatch? {
		val food = foodById.values.firstOrNull { candidate ->
			candidate.normalizedName == normalizedName
		} ?: return null
		return NutritionFoodMatch(
			foodId = food.id,
			matchSource = MATCH_SOURCE_NAME,
			confidence = NAME_MATCH_CONFIDENCE,
		)
	}

	private fun matchBestPrefix(normalizedName: String): NutritionFoodMatch? {
		val bestPrefixMatch = foodById.values
			.filter { food ->
				food.normalizedName.startsWith(normalizedName) ||
					normalizedName.startsWith(food.normalizedName)
			}
			.maxByOrNull { food -> food.normalizedName.length }
			?: return null
		return NutritionFoodMatch(
			foodId = bestPrefixMatch.id,
			matchSource = MATCH_SOURCE_NAME,
			confidence = PREFIX_MATCH_CONFIDENCE,
		)
	}

	private companion object {
		const val MATCH_SOURCE_ALIAS = "alias"
		const val MATCH_SOURCE_NAME = "name"
		val ALIAS_MATCH_CONFIDENCE = BigDecimal("1.00")
		val NAME_MATCH_CONFIDENCE = BigDecimal("0.90")
		val PREFIX_MATCH_CONFIDENCE = BigDecimal("0.75")
	}
}

package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal data class NutritionFoodRecord(
	val id: Int,
	val displayName: String,
	val normalizedName: String,
	val nutrients: FdcNutrientsPer100g,
	val sourceName: String = FDC_FOUNDATION_SOURCE_NAME,
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
		val lookupQueries = listOf(
			NutritionNameNormalizer.normalize(parsedName),
			NutritionNameNormalizer.forLookup(parsedName),
		).distinct().filter { query -> query.isNotBlank() }
		if (lookupQueries.isEmpty()) {
			return null
		}

		return lookupQueries.firstNotNullOfOrNull { query ->
			matchAlias(query) ?: matchExactName(query)
		} ?: matchBestTokenScore(NutritionNameNormalizer.forLookup(parsedName))
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
		val matches = foodById.values.filter { candidate ->
			candidate.normalizedName == normalizedName
		}
		val food = matches.firstOrNull { candidate -> !candidate.isBranded() }
			?: matches.firstOrNull()
			?: return null
		return NutritionFoodMatch(
			foodId = food.id,
			matchSource = MATCH_SOURCE_NAME,
			confidence = NAME_MATCH_CONFIDENCE,
		)
	}

	private fun matchBestTokenScore(queryNormalized: String): NutritionFoodMatch? =
		bestTokenMatch(queryNormalized)?.let { food ->
			NutritionFoodMatch(
				foodId = food.id,
				matchSource = MATCH_SOURCE_TOKENS,
				confidence = TOKEN_MATCH_CONFIDENCE,
			)
		}

	private fun bestTokenMatch(queryNormalized: String): NutritionFoodRecord? {
		if (queryNormalized.isBlank()) {
			return null
		}
		val scored = foodById.values.mapNotNull { food ->
			val score = NutritionFoodNameScorer.score(queryNormalized, food.normalizedName)
				?: return@mapNotNull null
			score to food
		}
		return pickBestScoredFood(scored.filterNot { candidate -> candidate.second.isBranded() })
			?: pickBestScoredFood(scored)
	}

	private fun pickBestScoredFood(
		scored: List<Pair<NutritionFoodNameScore, NutritionFoodRecord>>,
	): NutritionFoodRecord? =
		scored.maxWithOrNull(
			compareByDescending<Pair<NutritionFoodNameScore, NutritionFoodRecord>> { it.first.score }
				.thenBy { FdcFoodMatchingSupport.sourcePriority(it.second.sourceName) }
				.thenBy { it.first.extraTokenCount }
				.thenBy { it.second.normalizedName.length },
		)?.second

	private fun NutritionFoodRecord.isBranded(): Boolean =
		sourceName == FDC_BRANDED_SOURCE_NAME

	companion object {

		val EMPTY = NutritionLookupIndex(
			foodById = emptyMap(),
			foodIdByNormalizedAlias = emptyMap(),
			measuresByFoodId = emptyMap(),
		)

		private const val MATCH_SOURCE_ALIAS = "alias"
		private const val MATCH_SOURCE_NAME = "name"
		private const val MATCH_SOURCE_TOKENS = "tokens"
		private val ALIAS_MATCH_CONFIDENCE = BigDecimal("1.00")
		private val NAME_MATCH_CONFIDENCE = BigDecimal("0.90")
		private val TOKEN_MATCH_CONFIDENCE = BigDecimal("0.80")
	}
}

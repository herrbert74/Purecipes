package app.purecipes.backend.feature.nutrition

internal class FdcNeededFoodAccumulator(
	private val neededQueries: Set<String>?,
) {

	private val collectedFoods = mutableListOf<FdcFoundationFood>()
	private val queries = neededQueries.orEmpty().mapNotNull { query ->
		query.takeIf { value -> value.isNotBlank() }?.let { lookup ->
			NeededQuery(lookup = lookup, tokens = NutritionNameNormalizer.tokens(lookup))
		}
	}.filter { query -> query.tokens.isNotEmpty() }
	private val queriesByToken: Map<String, List<Int>> = buildMap<String, MutableList<Int>> {
		queries.forEachIndexed { index, query ->
			query.tokens.forEach { token ->
				tokenVariants(token).forEach { variant ->
					getOrPut(variant) { mutableListOf() }.add(index)
				}
			}
		}
	}
	private val bestByQueryIndex = arrayOfNulls<ScoredFood>(queries.size)

	fun consider(food: FdcFoundationFood) {
		if (neededQueries == null) {
			collectedFoods += food
			return
		}
		if (food.nutrientsPer100g() == null) {
			return
		}
		val seenQueryIndexes = mutableSetOf<Int>()
		NutritionNameNormalizer.tokens(food.normalizedDescription).forEach { token ->
			tokenVariants(token).forEach { variant ->
				queriesByToken[variant].orEmpty().forEach { queryIndex ->
					if (seenQueryIndexes.add(queryIndex)) {
						considerQuery(queryIndex, food)
					}
				}
			}
		}
	}

	fun selectedFoods(): List<FdcFoundationFood> =
		if (neededQueries == null) {
			collectedFoods
		} else {
			bestByQueryIndex.mapNotNull { scored -> scored?.food }.distinctBy { food -> food.fdcId }
		}

	fun neededNameMatches(): Map<String, String> =
		buildMap {
			queries.forEachIndexed { index, query ->
				val food = bestByQueryIndex[index]?.food ?: return@forEachIndexed
				put(query.lookup, food.description)
			}
		}

	private fun considerQuery(queryIndex: Int, food: FdcFoundationFood) {
		val query = queries[queryIndex]
		val scored = NutritionFoodNameScorer.score(query.lookup, food.normalizedDescription) ?: return
		if (!isAcceptableBrandedCandidate(query, food, scored)) {
			return
		}
		val candidate = ScoredFood(score = scored, food = food)
		val current = bestByQueryIndex[queryIndex]
		if (current == null || FOOD_COMPARATOR.compare(candidate, current) > 0) {
			bestByQueryIndex[queryIndex] = candidate
		}
	}

	private fun isAcceptableBrandedCandidate(
		query: NeededQuery,
		food: FdcFoundationFood,
		scored: NutritionFoodNameScore,
	): Boolean {
		if (food.sourceName != FDC_BRANDED_SOURCE_NAME) {
			return true
		}
		val withinExtraTokenLimit = scored.extraTokenCount <= MAX_BRANDED_EXTRA_TOKENS
		val phraseOk = query.tokens.size < MIN_TOKENS_FOR_PHRASE_MATCH ||
			food.normalizedDescription.contains(query.lookup)
		val leadingName = descriptionStartsWithQuery(food.normalizedDescription, query.lookup)
		val noDessertNoise = !hasConflictingBrandedExtras(query, food)
		return withinExtraTokenLimit && phraseOk && leadingName && noDessertNoise
	}

	private fun descriptionStartsWithQuery(normalizedDescription: String, query: String): Boolean =
		normalizedDescription == query ||
			normalizedDescription.startsWith("$query ")

	private fun hasConflictingBrandedExtras(query: NeededQuery, food: FdcFoundationFood): Boolean {
		val queryTokens = query.tokens.flatMap(::tokenVariants).toSet()
		val extraTokens = NutritionNameNormalizer.tokens(food.normalizedDescription)
			.flatMap(::tokenVariants)
			.toSet() - queryTokens
		return extraTokens.any { token -> token in BRANDED_CONFLICT_EXTRA_TOKENS }
	}

	private data class NeededQuery(
		val lookup: String,
		val tokens: List<String>,
	)

	private data class ScoredFood(
		val score: NutritionFoodNameScore,
		val food: FdcFoundationFood,
	)

	private companion object {

		val FOOD_COMPARATOR: Comparator<ScoredFood> =
			compareByDescending<ScoredFood> { scored -> scored.score.score }
				.thenBy { scored -> FdcFoodMatchingSupport.sourcePriority(scored.food.sourceName) }
				.thenBy { scored -> scored.score.extraTokenCount }
				.thenBy { scored -> scored.food.description.length }
				.thenBy { scored -> scored.food.fdcId }

		fun tokenVariants(token: String): List<String> {
			val singular = if (
				token.endsWith("s") &&
				!token.endsWith("ss") &&
				token.length > MIN_PLURAL_TOKEN_LENGTH
			) {
				token.removeSuffix("s")
			} else {
				null
			}
			return listOfNotNull(token, singular)
		}

		private const val MIN_PLURAL_TOKEN_LENGTH = 3
		private const val MAX_BRANDED_EXTRA_TOKENS = 4
		private const val MIN_TOKENS_FOR_PHRASE_MATCH = 2
		private val BRANDED_CONFLICT_EXTRA_TOKENS = setOf(
			"bar",
			"bars",
			"cake",
			"candy",
			"chocolate",
			"cookie",
			"cookies",
			"drink",
			"ice",
			"juice",
			"pizza",
			"soda",
		)
	}
}

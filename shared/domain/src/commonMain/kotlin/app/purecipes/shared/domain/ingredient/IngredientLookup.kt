package app.purecipes.shared.domain.ingredient

object IngredientLookup {

	const val DEFAULT_MAX_EDIT_DISTANCE = 2
	private const val MIN_QUERY_LENGTH_FOR_LIKELY_MATCH = 3
	private const val EXACT_MATCH_CONFIDENCE = 1.0

	fun resolveCatalogueIngredient(
		query: String,
		catalogueItems: Collection<String>,
	): String? {
		val normalizedQuery = IngredientNameMatching.normalizeIngredientName(query)
		if (normalizedQuery.isBlank()) {
			return null
		}

		val exactMatch = catalogueItems.firstOrNull { catalogueItem ->
			IngredientNameMatching.normalizeIngredientName(catalogueItem) == normalizedQuery
		}
		return exactMatch ?: run {
			val queryVariants = IngredientNameMatching.ingredientVariants(query)
			catalogueItems.firstOrNull { catalogueItem ->
				IngredientNameMatching.ingredientVariants(catalogueItem).any { it in queryVariants }
			}
		}
	}

	fun classifyIngredientMatches(
		query: String,
		vocabulary: Collection<String>,
		maxEditDistance: Int = DEFAULT_MAX_EDIT_DISTANCE,
	): ClassifiedIngredientMatches {
		val normalizedQuery = IngredientNameMatching.normalizeIngredientName(query)
		if (normalizedQuery.isBlank()) {
			return ClassifiedIngredientMatches()
		}

		val distinctVocabulary = vocabulary.distinct()
		val exactMatches = distinctVocabulary.mapNotNull { candidate ->
			if (isExactIngredientMatch(query, candidate)) {
				ScoredIngredientMatch(
					ingredient = candidate,
					confidence = EXACT_MATCH_CONFIDENCE,
				)
			} else {
				null
			}
		}

		val exactIngredients = exactMatches.map { it.ingredient }.toSet()
		val likelyMatches = if (normalizedQuery.length < MIN_QUERY_LENGTH_FOR_LIKELY_MATCH) {
			emptyList()
		} else {
			distinctVocabulary.mapNotNull { candidate ->
				if (candidate in exactIngredients) {
					return@mapNotNull null
				}

				val distance = minimumVariantEditDistance(query, candidate)
				if (distance in 1..maxEditDistance) {
					ScoredIngredientMatch(
						ingredient = candidate,
						confidence = likelyMatchConfidence(
							editDistance = distance,
							query = normalizedQuery,
							candidate = IngredientNameMatching.normalizeIngredientName(candidate),
						),
					)
				} else {
					null
				}
			}.sortedByDescending { it.confidence }
		}

		return ClassifiedIngredientMatches(
			exactMatches = exactMatches,
			likelyMatches = likelyMatches,
		)
	}

	fun findLikelyIngredientMatches(
		query: String,
		vocabulary: Collection<String>,
		maxEditDistance: Int = DEFAULT_MAX_EDIT_DISTANCE,
	): List<ScoredIngredientMatch> =
		classifyIngredientMatches(
			query = query,
			vocabulary = vocabulary,
			maxEditDistance = maxEditDistance,
		).likelyMatches

	internal fun isExactIngredientMatch(query: String, candidate: String): Boolean {
		if (IngredientNameMatching.normalizeIngredientName(query) ==
			IngredientNameMatching.normalizeIngredientName(candidate)
		) {
			return true
		}

		val queryVariants = IngredientNameMatching.ingredientVariants(query)
		val candidateVariants = IngredientNameMatching.ingredientVariants(candidate)
		return queryVariants.any { it in candidateVariants }
	}

	private fun minimumVariantEditDistance(query: String, candidate: String): Int {
		val queryVariants = IngredientNameMatching.ingredientVariants(query)
		val candidateVariants = IngredientNameMatching.ingredientVariants(candidate)
		return queryVariants.minOf { queryVariant ->
			candidateVariants.minOf { candidateVariant ->
				levenshteinDistance(queryVariant, candidateVariant)
			}
		}
	}

	private fun likelyMatchConfidence(
		editDistance: Int,
		query: String,
		candidate: String,
	): Double {
		val maxLength = maxOf(query.length, candidate.length)
		if (maxLength == 0) {
			return EXACT_MATCH_CONFIDENCE
		}
		return 1.0 - (editDistance.toDouble() / maxLength)
	}

	private fun levenshteinDistance(left: String, right: String): Int = when {
		left == right -> 0
		left.isEmpty() -> right.length
		right.isEmpty() -> left.length
		else -> computeLevenshteinDistance(left, right)
	}

	private fun computeLevenshteinDistance(left: String, right: String): Int {
		var previousRow = IntArray(right.length + 1) { it }
		var currentRow = IntArray(right.length + 1)

		for (rowIndex in left.indices) {
			currentRow[0] = rowIndex + 1
			for (columnIndex in right.indices) {
				val substitutionCost = if (left[rowIndex] == right[columnIndex]) 0 else 1
				currentRow[columnIndex + 1] = minOf(
					currentRow[columnIndex] + 1,
					previousRow[columnIndex + 1] + 1,
					previousRow[columnIndex] + substitutionCost,
				)
			}
			val swap = previousRow
			previousRow = currentRow
			currentRow = swap
		}

		return previousRow[right.length]
	}
}

package app.purecipes.backend.feature.nutrition

internal object FdcFoodMatcher {
	private val penaltyTerms = listOf(
		"restaurant",
		"canned",
		"frozen",
		"prepared",
		"dried",
		"sweetened",
		"breaded",
		"powder",
		"extract",
	)

	private val bonusTerms = listOf(
		"raw",
		"whole",
		"plain",
		"unenriched",
		"unsweetened",
	)

	private const val MINIMUM_MATCH_SCORE = 15
	private const val EXACT_MATCH_SCORE = 100
	private const val PREFIX_MATCH_SCORE = 50
	private const val TOKEN_MATCH_SCORE = 10
	private const val PENALTY_SCORE = 20
	private const val BONUS_SCORE = 5

	fun matchCatalogueName(
		catalogueName: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val preferredDescription = NutritionSeedOverrides.preferredDescriptionByCatalogueName[catalogueName]
		if (preferredDescription != null) {
			return foods.firstOrNull { it.description == preferredDescription }
		}
		return matchSearchTerm(catalogueName, foods)
	}

	fun matchAlias(
		alias: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val normalizedAlias = NutritionNameNormalizer.normalize(alias)
		val preferredDescription = NutritionSeedAliases.aliases
			.firstOrNull { NutritionNameNormalizer.normalize(it.alias) == normalizedAlias }
			?.preferredDescription
		if (preferredDescription != null) {
			return foods.firstOrNull { it.description == preferredDescription }
		}
		return matchSearchTerm(alias, foods)
	}

	private fun matchSearchTerm(
		searchTerm: String,
		foods: List<FdcFoundationFood>,
	): FdcFoundationFood? {
		val normalizedTerm = NutritionNameNormalizer.normalize(searchTerm)
		if (normalizedTerm.isBlank()) {
			return null
		}
		val tokens = normalizedTerm.split(' ').filter { it.length > 2 }

		return foods
			.mapNotNull { food ->
				val score = scoreFood(food, normalizedTerm, tokens)
				if (score < MINIMUM_MATCH_SCORE) {
					null
				} else {
					score to food
				}
			}
			.maxWithOrNull(
				compareByDescending<Pair<Int, FdcFoundationFood>> { it.first }
					.thenBy { FdcFoodMatchingSupport.sourcePriority(it.second.sourceName) },
			)
			?.second
	}

	private fun scoreFood(
		food: FdcFoundationFood,
		normalizedTerm: String,
		tokens: List<String>,
	): Int {
		val description = food.normalizedDescription
		if (!description.contains(normalizedTerm) && tokens.none { token -> description.contains(token) }) {
			return 0
		}

		var score = 0
		if (description == normalizedTerm) {
			score += EXACT_MATCH_SCORE
		}
		if (description.startsWith(normalizedTerm)) {
			score += PREFIX_MATCH_SCORE
		}
		tokens.forEach { token ->
			if (description.contains(token)) {
				score += TOKEN_MATCH_SCORE
			}
		}
		penaltyTerms.forEach { term ->
			if (description.contains(term)) {
				score -= PENALTY_SCORE
			}
		}
		bonusTerms.forEach { term ->
			if (description.contains(term)) {
				score += BONUS_SCORE
			}
		}
		return score
	}
}

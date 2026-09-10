package app.purecipes.backend.feature.nutrition

internal data class NutritionFoodNameScore(
	val score: Int,
	val extraTokenCount: Int,
)

internal object NutritionFoodNameScorer {

	private val penaltyTerms = setOf(
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

	private val bonusTerms = setOf(
		"raw",
		"whole",
		"plain",
		"unenriched",
		"unsweetened",
	)

	private val conflictTokens = setOf(
		"spread",
		"margarine",
		"substitute",
		"dressing",
		"mayonnaise",
		"soup",
		"stew",
		"mix",
		"mixed",
		"blend",
		"blended",
		"flavored",
		"flavour",
		"candy",
		"pudding",
		"sandwich",
		"butter",
		"butterbur",
	)

	private val insufficientSoloQueryTokens = setOf(
		"box",
		"side",
	)

	fun score(queryNormalized: String, candidateNormalized: String): NutritionFoodNameScore? {
		val queryTokens = NutritionNameNormalizer.tokens(queryNormalized)
		val queryCanonical = queryTokens.flatMap(::expandPlural).toSet()
		val candidateTokens = canonicalTokens(candidateNormalized)
		val extraTokens = candidateTokens - queryCanonical
		val tokenScore = queryTokens.size * TOKEN_MATCH_SCORE + extraTokenAdjustment(extraTokens)
		return when {
			queryTokens.isEmpty() -> null
			queryTokens.size == 1 && queryTokens.first() in insufficientSoloQueryTokens -> null
			!hasAllQueryTokens(queryTokens, candidateTokens) -> null
			extraTokens.any { token -> token in conflictTokens } -> null
			tokenScore < MINIMUM_MATCH_SCORE -> null
			else -> NutritionFoodNameScore(score = tokenScore, extraTokenCount = extraTokens.size)
		}
	}

	private fun hasAllQueryTokens(
		queryTokens: List<String>,
		candidateTokens: Set<String>,
	): Boolean =
		queryTokens.all { token ->
			expandPlural(token).any { variant -> variant in candidateTokens }
		}

	private fun extraTokenAdjustment(extraTokens: Set<String>): Int =
		extraTokens.sumOf { token ->
			val bonus = if (token in bonusTerms) BONUS_SCORE else 0
			val penalty = if (token in penaltyTerms) PENALTY_SCORE else 0
			bonus - penalty
		}

	private fun canonicalTokens(normalized: String): Set<String> =
		NutritionNameNormalizer.tokens(normalized).flatMap(::expandPlural).toSet()

	private fun expandPlural(token: String): List<String> {
		val singular = if (token.endsWith("s") && !token.endsWith("ss") && token.length > MIN_PLURAL_TOKEN_LENGTH) {
			token.removeSuffix("s")
		} else {
			null
		}
		return listOfNotNull(token, singular)
	}

	private const val MINIMUM_MATCH_SCORE = 15
	private const val TOKEN_MATCH_SCORE = 15
	private const val PENALTY_SCORE = 20
	private const val BONUS_SCORE = 5
	private const val MIN_PLURAL_TOKEN_LENGTH = 3
}

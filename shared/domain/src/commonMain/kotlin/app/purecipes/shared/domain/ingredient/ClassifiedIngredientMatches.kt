package app.purecipes.shared.domain.ingredient

data class ClassifiedIngredientMatches(
	val exactMatches: List<ScoredIngredientMatch> = emptyList(),
	val likelyMatches: List<ScoredIngredientMatch> = emptyList(),
)

data class ScoredIngredientMatch(
	val ingredient: String,
	val confidence: Double,
)

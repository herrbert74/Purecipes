package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientMatchResponse(
	val query: String,
	val exactMatches: List<IngredientMatchCount> = emptyList(),
	val likelyMatches: List<LikelyIngredientMatch> = emptyList(),
)

@Serializable
data class IngredientMatchCount(
	val ingredient: String,
	val recipeCount: Int,
)

@Serializable
data class LikelyIngredientMatch(
	val ingredient: String,
	val recipeCount: Int,
	val confidence: Double,
)

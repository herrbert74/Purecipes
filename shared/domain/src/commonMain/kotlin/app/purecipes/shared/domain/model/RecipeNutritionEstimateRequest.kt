package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeNutritionEstimateRequest(
	val ingredients: List<String> = emptyList(),
)

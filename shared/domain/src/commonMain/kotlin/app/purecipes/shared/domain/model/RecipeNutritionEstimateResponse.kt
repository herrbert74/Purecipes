package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeNutritionEstimateResponse(
	val nutrition: NutritionSummary? = null,
)

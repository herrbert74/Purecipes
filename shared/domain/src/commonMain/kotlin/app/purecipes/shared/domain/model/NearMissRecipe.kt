package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NearMissRecipe(
	val recipe: RecipeSummary,
	val missingIngredient: String,
)

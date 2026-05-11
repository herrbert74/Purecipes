package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeWriteRequest(
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredientGroups: List<IngredientGroup> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: Cuisine? = null,
)

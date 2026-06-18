package app.purecipes.feature.newrecipe.domain.model

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeIngredient

data class SaveCreatedRecipeRequest(
	val recipeId: Int? = null,
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredients: List<RecipeIngredient> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: Cuisine? = null,
)

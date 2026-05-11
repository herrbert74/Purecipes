package app.purecipes.feature.newrecipe.domain.model

import app.purecipes.shared.domain.model.Cuisine

data class SaveCreatedRecipeRequest(
	val recipeId: Int? = null,
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredients: List<String> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: Cuisine? = null,
)

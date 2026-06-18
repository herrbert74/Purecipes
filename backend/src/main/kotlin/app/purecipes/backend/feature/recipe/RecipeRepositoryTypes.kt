package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeIngredient

internal data class RecipeRecord(
	val id: Int,
	val title: String,
	val description: String?,
	val instructions: String?,
	val totalTime: Int?,
	val yields: String?,
	val imageUrl: String?,
	val cuisine: String?,
	val mealType: String?,
	val measurementSystem: MeasurementSystem?,
	val difficulty: String?,
	val cookingMethod: String?,
	val calorieRange: String?,
	val dietaryPreferences: List<String>,
	val tags: List<String>,
)

internal data class IngredientGroupAccumulator(
	val name: String?,
	val ingredients: MutableList<RecipeIngredient> = mutableListOf(),
)

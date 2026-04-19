package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientGroup(
	val name: String? = null,
	val ingredients: List<String> = emptyList(),
)

@Serializable
data class RecipeDetails(
	val id: Int,
	val title: String,
	val description: String,
	val imageUrl: String? = null,
	val ingredientGroups: List<IngredientGroup> = emptyList(),
	val steps: List<String> = emptyList(),
	val totalTime: Int? = null,
	val yields: String? = null,
	val cuisine: Cuisine? = null,
	val measurementSystem: MeasurementSystem? = null,
	val isFavorite: Boolean = false,
	val mealType: MealType? = null,
	val difficultyLevel: DifficultyLevel? = null,
	val cookingMethod: CookingMethod? = null,
	val calorieRange: CalorieRange? = null,
	val dietaryPreferences: Set<DietaryPreference> = emptySet(),
	val tags: Set<String> = emptySet(),
)

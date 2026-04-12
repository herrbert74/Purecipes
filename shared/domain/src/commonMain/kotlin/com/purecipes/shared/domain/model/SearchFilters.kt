package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchFilters(
	val includeIngredients: Set<String> = emptySet(),
	val excludeIngredients: Set<String> = emptySet(),
	val dietaryPreferences: Set<DietaryPreference> = emptySet(),
	val cuisines: Set<Cuisine> = emptySet(),
	val mealTypes: Set<MealType> = emptySet(),
	val cookingTimeRanges: Set<CookingTimeRange> = emptySet(),
	val difficultyLevels: Set<DifficultyLevel> = emptySet(),
	val cookingMethods: Set<CookingMethod> = emptySet(),
	val calorieRanges: Set<CalorieRange> = emptySet(),
	val nutritionFilters: Set<NutritionFilter> = emptySet(),
) {

	val isEmpty: Boolean
		get() = includeIngredients.isEmpty() &&
			excludeIngredients.isEmpty() &&
			dietaryPreferences.isEmpty() &&
			cuisines.isEmpty() &&
			mealTypes.isEmpty() &&
			cookingTimeRanges.isEmpty() &&
			difficultyLevels.isEmpty() &&
			cookingMethods.isEmpty() &&
			calorieRanges.isEmpty() &&
			nutritionFilters.isEmpty()

	companion object {

		fun default() = SearchFilters(
			includeIngredients = setOf("Chicken", "Beef", "Pork", "Fish", "Eggs", "Tomato", "Onion", "Garlic"),
			dietaryPreferences = DietaryPreference.entries.toSet(),
			cuisines = Cuisine.entries.toSet(),
			mealTypes = MealType.entries.toSet(),
			cookingTimeRanges = CookingTimeRange.entries.toSet(),
			difficultyLevels = DifficultyLevel.entries.toSet(),
			cookingMethods = CookingMethod.entries.toSet(),
			calorieRanges = CalorieRange.entries.toSet(),
			nutritionFilters = NutritionFilter.entries.toSet(),
		)
	}
}

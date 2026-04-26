package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class SearchFilters(
	@JsonNames("includeIngredients")
	val availableIngredients: Set<String> = emptySet(),
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
		get() = availableIngredients.isEmpty() &&
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
			availableIngredients = setOf("Chicken", "Beef", "Pork", "Fish", "Eggs", "Tomato", "Onion", "Garlic"),
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

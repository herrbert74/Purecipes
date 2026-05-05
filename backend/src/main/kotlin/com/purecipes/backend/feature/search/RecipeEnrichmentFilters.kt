package com.purecipes.backend.feature.search

import com.purecipes.shared.domain.model.CalorieRange
import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType
import com.purecipes.shared.domain.model.NutritionFilter
import com.purecipes.shared.domain.model.SearchFilters

internal const val LOW_CARB_THRESHOLD = 20
internal const val HIGH_PROTEIN_THRESHOLD = 25
internal const val LOW_SODIUM_THRESHOLD = 600
internal const val LOW_FAT_THRESHOLD = 10
internal const val HIGH_FIBER_THRESHOLD = 5

internal fun addEnrichmentFilterConditions(
	filters: SearchFilters,
	conditions: MutableList<String>,
	params: MutableList<Any>,
) {
	if (filters.mealTypes.isNotEmpty() && filters.mealTypes.size < MealType.entries.size) {
		val placeholders = filters.mealTypes.joinToString(",") { "?" }
		conditions.add("r.meal_type IN ($placeholders)")
		filters.mealTypes.forEach { params.add(it.name) }
	}

	if (filters.difficultyLevels.isNotEmpty() && filters.difficultyLevels.size < DifficultyLevel.entries.size) {
		val placeholders = filters.difficultyLevels.joinToString(",") { "?" }
		conditions.add("r.difficulty IN ($placeholders)")
		filters.difficultyLevels.forEach { params.add(it.name) }
	}

	if (filters.cookingMethods.isNotEmpty() && filters.cookingMethods.size < CookingMethod.entries.size) {
		val placeholders = filters.cookingMethods.joinToString(",") { "?" }
		conditions.add("r.cooking_method IN ($placeholders)")
		filters.cookingMethods.forEach { params.add(it.name) }
	}

	if (filters.calorieRanges.isNotEmpty() && filters.calorieRanges.size < CalorieRange.entries.size) {
		val placeholders = filters.calorieRanges.joinToString(",") { "?" }
		conditions.add("r.calorie_range IN ($placeholders)")
		filters.calorieRanges.forEach { params.add(it.name) }
	}

	if (filters.dietaryPreferences.isNotEmpty()) {
		val parts = filters.dietaryPreferences.map { "? = ANY(r.dietary_preferences)" }
		conditions.add("(${parts.joinToString(" OR ")})")
		filters.dietaryPreferences.forEach { params.add(it.name) }
	}

	filters.nutritionFilters.forEach { filter ->
		val condition = when (filter) {
			NutritionFilter.LOW_CARB -> "n.carbohydrates < $LOW_CARB_THRESHOLD"
			NutritionFilter.HIGH_PROTEIN -> "n.protein > $HIGH_PROTEIN_THRESHOLD"
			NutritionFilter.LOW_SODIUM -> "n.sodium < $LOW_SODIUM_THRESHOLD"
			NutritionFilter.LOW_FAT -> "n.fat < $LOW_FAT_THRESHOLD"
			NutritionFilter.HIGH_FIBER -> "n.fiber > $HIGH_FIBER_THRESHOLD"
		}
		conditions.add(
			"""
			EXISTS (
				SELECT 1 FROM nutrition n
				WHERE n.recipe_id = r.id AND $condition
			)
			""".trimIndent(),
		)
	}
}

package app.purecipes.backend.feature.search

import app.purecipes.shared.domain.model.CalorieRange
import app.purecipes.shared.domain.model.CookingMethod
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.NutritionFilter
import app.purecipes.shared.domain.model.SearchFilters

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
	addEnumColumnFilter(
		selected = filters.mealTypes,
		allValues = MealType.entries,
		column = "r.meal_type",
		params = params,
		conditions = conditions,
		valueSelector = MealType::name,
	)
	addEnumColumnFilter(
		selected = filters.difficultyLevels,
		allValues = DifficultyLevel.entries,
		column = "r.difficulty",
		params = params,
		conditions = conditions,
		valueSelector = DifficultyLevel::name,
	)
	addEnumColumnFilter(
		selected = filters.cookingMethods,
		allValues = CookingMethod.entries,
		column = "r.cooking_method",
		params = params,
		conditions = conditions,
		valueSelector = CookingMethod::name,
	)
	addEnumColumnFilter(
		selected = filters.calorieRanges,
		allValues = CalorieRange.entries,
		column = "r.calorie_range",
		params = params,
		conditions = conditions,
		valueSelector = CalorieRange::name,
	)
	addDietaryPreferenceFilters(filters, conditions, params)
	addNutritionFilters(filters, conditions)
}

private fun <T> addEnumColumnFilter(
	selected: Set<T>,
	allValues: List<T>,
	column: String,
	params: MutableList<Any>,
	conditions: MutableList<String>,
	valueSelector: (T) -> String,
) {
	if (selected.isEmpty() || selected.size >= allValues.size) {
		return
	}
	val placeholders = selected.joinToString(",") { "?" }
	conditions.add("$column IN ($placeholders)")
	selected.forEach { value -> params.add(valueSelector(value)) }
}

private fun addDietaryPreferenceFilters(
	filters: SearchFilters,
	conditions: MutableList<String>,
	params: MutableList<Any>,
) {
	if (filters.dietaryPreferences.isEmpty()) {
		return
	}
	val parts = filters.dietaryPreferences.map { "? = ANY(r.dietary_preferences)" }
	conditions.add("(${parts.joinToString(" OR ")})")
	filters.dietaryPreferences.forEach { params.add(it.name) }
}

private fun addNutritionFilters(
	filters: SearchFilters,
	conditions: MutableList<String>,
) {
	filters.nutritionFilters.forEach { filter ->
		val condition = nutritionFilterCondition(filter)
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

private fun nutritionFilterCondition(filter: NutritionFilter): String =
	when (filter) {
		NutritionFilter.LOW_CARB -> "n.carbohydrates < $LOW_CARB_THRESHOLD"
		NutritionFilter.HIGH_PROTEIN -> "n.protein > $HIGH_PROTEIN_THRESHOLD"
		NutritionFilter.LOW_SODIUM -> "n.sodium < $LOW_SODIUM_THRESHOLD"
		NutritionFilter.LOW_FAT -> "n.fat < $LOW_FAT_THRESHOLD"
		NutritionFilter.HIGH_FIBER -> "n.fiber > $HIGH_FIBER_THRESHOLD"
	}

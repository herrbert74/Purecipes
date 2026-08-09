package app.purecipes.feature.analytics.domain.model

import app.purecipes.shared.domain.model.SearchFilters

data class SearchPerformedContext(
	val query: String,
	val resultCount: Int,
	val filters: SearchFilters,
	val pantryCount: Int,
	val excludedCount: Int,
	val keyIngredientCount: Int,
	val nearMissCount: Int,
	val isPremiumUser: Boolean,
)

internal fun SearchFilters.selectedValueCount(): Int =
	dietaryPreferences.size +
		cuisines.size +
		mealTypes.size +
		cookingTimeRanges.size +
		difficultyLevels.size +
		cookingMethods.size +
		calorieRanges.size +
		nutritionFilters.size

internal fun <T> Set<T>.toJoinedDisplayNames(label: (T) -> String): String =
	map(label).sorted().joinToString(",")

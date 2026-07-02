package app.purecipes.backend.feature.search

import app.purecipes.shared.domain.model.CookingTimeRange
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.SearchFilters

internal fun addCuisineFilterConditions(
	filters: SearchFilters,
	conditions: MutableList<String>,
	params: MutableList<Any>,
) {
	if (filters.cuisines.isEmpty() || filters.cuisines.size >= Cuisine.entries.size) {
		return
	}
	val placeholders = filters.cuisines.joinToString(",") { "?" }
	conditions.add("r.cuisine IN ($placeholders)")
	filters.cuisines.forEach { params.add(it.displayName) }
}

internal fun addCookingTimeFilterConditions(
	filters: SearchFilters,
	conditions: MutableList<String>,
) {
	if (filters.cookingTimeRanges.isEmpty() || filters.cookingTimeRanges.size >= CookingTimeRange.entries.size) {
		return
	}
	val timeParts = filters.cookingTimeRanges.map(::cookingTimeRangeCondition)
	conditions.add("(${timeParts.joinToString(" OR ")})")
}

private fun cookingTimeRangeCondition(range: CookingTimeRange): String =
	when (range) {
		CookingTimeRange.UNDER_15 -> "r.total_time <= 15"
		CookingTimeRange.UNDER_30 -> "r.total_time <= 30"
		CookingTimeRange.UNDER_60 -> "r.total_time <= 60"
		CookingTimeRange.OVER_60 -> "r.total_time > 60"
	}

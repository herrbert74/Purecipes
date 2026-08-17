package app.purecipes.feature.search.ui

internal const val SEARCH_FILTER_NOTE_TAG = "searchFilterNote"

internal fun formatSearchFilterNote(
	isTitleSearch: Boolean,
	hasPantry: Boolean,
	hasExclusions: Boolean,
	hasRecipeFilters: Boolean,
	applyRecipeFiltersToTitleSearch: Boolean,
): String? {
	val parts = mutableListOf<String>()
	if (!isTitleSearch && hasPantry) {
		parts += "Showing recipes you can make with your pantry."
	}
	if (hasExclusions) {
		parts += "Recipes with excluded ingredients are hidden."
	}
	when {
		isTitleSearch && hasRecipeFilters && applyRecipeFiltersToTitleSearch ->
			parts += "Diet and other filters still apply — change this in Settings."

		isTitleSearch && hasRecipeFilters ->
			parts += "Diet and other filters are off for this search — change this in Settings."

		!isTitleSearch && hasRecipeFilters ->
			parts += "Diet and other filters still apply. Change them with the filter button."
	}
	return parts.joinToString(separator = " ").takeIf { it.isNotEmpty() }
}

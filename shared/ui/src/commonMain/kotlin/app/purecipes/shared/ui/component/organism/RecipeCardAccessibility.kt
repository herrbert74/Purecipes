package app.purecipes.shared.ui.component.organism

import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary

internal const val QUICK_RECIPE_MAX_MINUTES = 30
internal const val PREP_TIME_UNKNOWN = "Prep time unknown"
internal const val MAX_DESCRIBED_CARD_APPLIANCES = 3

internal fun MeasurementSystem.recipeCardLabel(): String = when (this) {
	MeasurementSystem.IMPERIAL -> "Imperial"
	MeasurementSystem.METRIC -> "Metric"
	MeasurementSystem.MIXED -> "Mixed"
}

internal fun recipeCardContentDescription(
	recipe: RecipeSummary,
	favoritedLabel: String,
	includeLabels: Boolean,
): String {
	val parts = buildList {
		add(recipe.title)
		if (includeLabels) {
			addAll(recipeCardLabels(recipe))
		}
		add(recipe.totalTime?.let { minutes -> "$minutes min" } ?: PREP_TIME_UNKNOWN)
		if (recipe.isFavorite) {
			add(favoritedLabel)
		}
		addAll(recipe.appliances.accessibilityLabels())
	}
	return parts.joinToString(separator = ", ")
}

private fun recipeCardLabels(recipe: RecipeSummary): List<String> = buildList {
	recipe.totalTime
		?.takeIf { minutes -> minutes <= QUICK_RECIPE_MAX_MINUTES }
		?.let { add("Quick") }
	recipe.cuisine?.displayName?.let { add(it) }
	recipe.measurementSystem?.let { add(it.recipeCardLabel()) }
	if (recipe.isPrivate) {
		add("Private")
	}
}

private fun Set<Appliance>.accessibilityLabels(): List<String> {
	return take(MAX_DESCRIBED_CARD_APPLIANCES).map { appliance -> appliance.displayName }
}

package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeFormatHandling
import app.purecipes.shared.domain.model.RecipeSummary

class FilterRecipesForMeasurementPreferencesUseCase {

	operator fun invoke(
		recipes: List<RecipeSummary>,
		preferences: MeasurementPreferences,
	): List<RecipeSummary> {
		if (preferences.formatHandling != RecipeFormatHandling.FILTER_OUT) {
			return recipes
		}

		return recipes.filter { summary ->
			val recipeSystem = summary.measurementSystem
			recipeSystem == null ||
				recipeSystem == MeasurementSystem.MIXED ||
				recipeSystem == preferences.preferredSystem
		}
	}
}

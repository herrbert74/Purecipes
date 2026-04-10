package com.purecipes.feature.measurement.domain.usecase

import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeFormatHandling
import com.purecipes.shared.domain.model.RecipeSummary

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

package app.purecipes.feature.measurement.domain.model

import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails

data class ProcessedRecipeDetails(
	val recipe: RecipeDetails,
	val originalMeasurementSystem: MeasurementSystem?,
	val isConverted: Boolean,
	val shouldShowMismatchNotification: Boolean,
)

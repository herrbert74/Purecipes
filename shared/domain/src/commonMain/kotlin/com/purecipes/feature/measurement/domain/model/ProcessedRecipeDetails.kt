package com.purecipes.feature.measurement.domain.model

import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails

data class ProcessedRecipeDetails(
	val recipe: RecipeDetails,
	val originalMeasurementSystem: MeasurementSystem?,
	val isConverted: Boolean,
	val shouldShowMismatchNotification: Boolean,
)

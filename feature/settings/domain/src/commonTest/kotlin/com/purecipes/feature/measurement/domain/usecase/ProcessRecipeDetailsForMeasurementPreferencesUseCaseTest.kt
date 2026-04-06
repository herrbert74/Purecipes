package com.purecipes.feature.measurement.domain.usecase

import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeFormatHandling
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProcessRecipeDetailsForMeasurementPreferencesUseCaseTest {

	@Test
	fun `mixed measurement recipes stay unchanged when user prefers metric conversion`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 1,
			title = "Soup",
			description = "Mixed measurement recipe",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf("8 cups (1.9L) stock"),
				),
			),
			steps = listOf("Bake at 350F"),
			measurementSystem = MeasurementSystem.MIXED,
		)
		val preferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
		)

		val result = useCase(recipe, preferences)

		assertFalse(result.isConverted)
		assertFalse(result.shouldShowMismatchNotification)
		assertEquals(MeasurementSystem.MIXED, result.recipe.measurementSystem)
		assertEquals("8 cups (1.9L) stock", result.recipe.ingredientGroups.single().ingredients.single())
		assertEquals("Bake at 350F", result.recipe.steps.single())
	}
}

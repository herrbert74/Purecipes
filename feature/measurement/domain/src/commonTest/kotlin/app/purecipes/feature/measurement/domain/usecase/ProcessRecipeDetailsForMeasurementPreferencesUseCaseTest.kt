package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
import io.kotest.matchers.shouldBe
import kotlin.test.Test

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

		result.isConverted shouldBe false
		result.shouldShowMismatchNotification shouldBe false
		result.recipe.measurementSystem shouldBe MeasurementSystem.MIXED
		result.recipe.ingredientGroups.single().ingredients.single() shouldBe "8 cups (1.9L) stock"
		result.recipe.steps.single() shouldBe "Bake at 350F"
	}
}

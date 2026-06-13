package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class ProcessRecipeDetailsForMeasurementPreferencesUseCaseTest {

	@Test
	fun `convert to preferred transforms imperial recipe ingredients`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 1,
			title = "Cake",
			description = "Imperial recipe",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf("2 cups flour"),
				),
			),
			steps = listOf("Bake at 350F"),
			measurementSystem = MeasurementSystem.IMPERIAL,
		)
		val preferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
		)

		val result = useCase(recipe, preferences)

		result.isConverted shouldBe true
		result.recipe.ingredientGroups.single().ingredients.single() shouldContain "mL"
		result.recipe.steps.single() shouldContain "C"
	}

	@Test
	fun `convert to preferred detects measurement system when metadata is missing`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 2,
			title = "Bread",
			description = "Imperial recipe without metadata",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf("3 lb bread flour"),
				),
			),
			steps = emptyList(),
			measurementSystem = null,
		)
		val preferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
		)

		val result = useCase(recipe, preferences)

		result.isConverted shouldBe true
		result.recipe.ingredientGroups.single().ingredients.single() shouldContain "kg"
	}

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

package app.purecipes.feature.measurement.domain.usecase

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
import app.purecipes.shared.domain.model.RecipeIngredient
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
					ingredients = listOf(RecipeIngredient(text = "2 cups flour")),
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
		result.recipe.ingredientGroups.single().ingredients.single().text shouldContain "mL"
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
					ingredients = listOf(RecipeIngredient(text = "3 lb bread flour")),
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
		result.recipe.ingredientGroups.single().ingredients.single().text shouldContain "kg"
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
					ingredients = listOf(RecipeIngredient(text = "8 cups (1.9L) stock")),
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
		result.recipe.ingredientGroups.single().ingredients.single().text shouldBe "8 cups (1.9L) stock"
		result.recipe.steps.single() shouldBe "Bake at 350F"
	}

	@Test
	fun `convert to preferred keeps tablespoon and teaspoon unchanged when converting imperial to metric`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 1,
			title = "Pancakes",
			description = "Imperial recipe with tbsp and tsp",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf(
						RecipeIngredient(text = "2 cups flour"),
						RecipeIngredient(text = "8 oz cream cheese"),
						RecipeIngredient(text = "1 tbsp sugar"),
						RecipeIngredient(text = "2 tablespoons butter"),
						RecipeIngredient(text = "1 tsp salt"),
						RecipeIngredient(text = "1/2 teaspoon vanilla extract"),
					),
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
		result.originalMeasurementSystem shouldBe MeasurementSystem.IMPERIAL
		result.recipe.measurementSystem shouldBe MeasurementSystem.METRIC
		val ingredients = result.recipe.ingredientGroups.single().ingredients.map { it.text }
		ingredients shouldBe listOf(
			"473 mL flour",
			"227 g cream cheese",
			"1 tbsp sugar",
			"2 tablespoons butter",
			"1 tsp salt",
			"1/2 teaspoon vanilla extract",
		)
		result.recipe.steps.single() shouldBe "Bake at 175°C"
	}

	@Test
	fun `metric recipe with tablespoon and teaspoon is detected as metric rather than mixed`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 2,
			title = "Crepes",
			description = "Metric recipe with tbsp and tsp without metadata",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf(
						RecipeIngredient(text = "200 g flour"),
						RecipeIngredient(text = "300 mL milk"),
						RecipeIngredient(text = "1 tbsp olive oil"),
						RecipeIngredient(text = "2 tablespoons melted butter"),
						RecipeIngredient(text = "1 tsp salt"),
						RecipeIngredient(text = "1/2 teaspoon baking powder"),
					),
				),
			),
			steps = listOf("Cook at 200°C"),
			measurementSystem = null,
		)
		val preferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
		)

		val result = useCase(recipe, preferences)

		result.isConverted shouldBe false
		result.shouldShowMismatchNotification shouldBe false
		result.originalMeasurementSystem shouldBe MeasurementSystem.METRIC
		val ingredients = result.recipe.ingredientGroups.single().ingredients.map { it.text }
		ingredients shouldBe listOf(
			"200 g flour",
			"300 mL milk",
			"1 tbsp olive oil",
			"2 tablespoons melted butter",
			"1 tsp salt",
			"1/2 teaspoon baking powder",
		)
	}

	@Test
	fun `convert to preferred keeps tablespoon and teaspoon unchanged when converting metric to imperial`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 3,
			title = "Crepes",
			description = "Metric recipe with tbsp and tsp",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf(
						RecipeIngredient(text = "200 g flour"),
						RecipeIngredient(text = "1 tbsp olive oil"),
						RecipeIngredient(text = "1 tsp salt"),
					),
				),
			),
			steps = listOf("Cook at 200°C"),
			measurementSystem = MeasurementSystem.METRIC,
		)
		val preferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.IMPERIAL,
			formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
		)

		val result = useCase(recipe, preferences)

		result.isConverted shouldBe true
		result.originalMeasurementSystem shouldBe MeasurementSystem.METRIC
		result.recipe.measurementSystem shouldBe MeasurementSystem.IMPERIAL
		val ingredients = result.recipe.ingredientGroups.single().ingredients.map { it.text }
		ingredients shouldBe listOf(
			"7 oz flour",
			"1 tbsp olive oil",
			"1 tsp salt",
		)
	}

	@Test
	fun `recipe with only tablespoon and teaspoon has no detectable mismatch`() {
		val useCase = ProcessRecipeDetailsForMeasurementPreferencesUseCase()
		val recipe = RecipeDetails(
			id = 4,
			title = "Dressing",
			description = "Only tbsp and tsp",
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = listOf(
						RecipeIngredient(text = "2 tbsp olive oil"),
						RecipeIngredient(text = "1 tsp lemon juice"),
					),
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

		result.isConverted shouldBe false
		result.shouldShowMismatchNotification shouldBe false
		result.originalMeasurementSystem shouldBe null
	}
}

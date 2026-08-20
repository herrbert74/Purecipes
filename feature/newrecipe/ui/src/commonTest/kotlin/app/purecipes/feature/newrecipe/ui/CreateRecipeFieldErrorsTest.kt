package app.purecipes.feature.newrecipe.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CreateRecipeFieldErrorsTest {

	@Test
	fun `first section is about when title is missing`() {
		CreateRecipeFieldErrors(title = CREATE_RECIPE_TITLE_REQUIRED_MESSAGE)
			.firstSection shouldBe CreateRecipeSection.About
	}

	@Test
	fun `first section is ingredients when only ingredient names are missing`() {
		CreateRecipeFieldErrors(unnamedIngredientIndexes = listOf(0))
			.firstSection shouldBe CreateRecipeSection.Ingredients
	}

	@Test
	fun `first section is steps when only steps are missing`() {
		CreateRecipeFieldErrors(steps = CREATE_RECIPE_STEP_REQUIRED_MESSAGE)
			.firstSection shouldBe CreateRecipeSection.Steps
	}

	@Test
	fun `ingredient name error is returned for flagged indexes`() {
		val errors = CreateRecipeFieldErrors(unnamedIngredientIndexes = listOf(1))

		errors.ingredientNameError(index = 0) shouldBe null
		errors.ingredientNameError(index = 1) shouldBe CREATE_RECIPE_INGREDIENT_NAME_REQUIRED_MESSAGE
	}
}

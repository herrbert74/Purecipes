package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientNutritionIssueTest {

	@Test
	fun collectIngredientIssuesFlagsUnmatchedFood() {
		val calculation = RecipeNutritionCalculator(NutritionLookupIndex.EMPTY).calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 1, rawText = "2 cups mystery spice"),
			),
		)

		val issues = calculation.collectIngredientIssues()
		issues.size shouldBe 1
		issues.single().kind shouldBe IngredientNutritionIssueKind.NO_FOOD_MATCH
		issues.single().parsedName shouldBe "mystery spice"
	}

	@Test
	fun collectIngredientIssuesFlagsUnmeasurableLine() {
		val calculation = RecipeNutritionCalculator(NutritionLookupIndex.EMPTY).calculate(
			listOf(
				RecipeIngredientRow(ingredientId = 2, rawText = "Salt to taste"),
			),
		)

		val issues = calculation.collectIngredientIssues()
		issues.single().kind shouldBe IngredientNutritionIssueKind.NOT_MEASURABLE
	}
}

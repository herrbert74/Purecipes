package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RecipePantryCoverageTest {

	@Test
	fun `optional slots are ignored when counting missing ingredients`() {
		val groups = listOf(
			IngredientGroup(
				ingredients = listOf(
					RecipeIngredient(text = "Chicken breast"),
					RecipeIngredient(
						text = "optional Basil, to garnish",
						requirement = IngredientRequirement.OPTIONAL,
					),
				),
			),
		)

		missingIngredientCount(groups, isSlotCovered = { false }) shouldBe 1
		singleMissingIngredientLabel(groups, isSlotCovered = { false }) shouldBe "Chicken breast"
	}

	@Test
	fun `alternative slot counts as one missing ingredient when none covered`() {
		val groups = listOf(
			IngredientGroup(
				ingredients = listOf(
					RecipeIngredient(text = "Tomato"),
					RecipeIngredient(
						text = "Chicken breast",
						requirement = IngredientRequirement.ALTERNATIVE,
						alternativeGroupKey = 1,
					),
					RecipeIngredient(
						text = "Tofu",
						requirement = IngredientRequirement.ALTERNATIVE,
						alternativeGroupKey = 1,
					),
				),
			),
		)

		missingIngredientCount(groups, isSlotCovered = { slot ->
			slot.any { ingredient -> ingredient.text == "Tomato" }
		}) shouldBe 1
		singleMissingIngredientLabel(groups, isSlotCovered = { slot ->
			slot.any { ingredient -> ingredient.text == "Tomato" }
		}) shouldBe "Chicken breast or Tofu"
	}

	@Test
	fun `two uncovered slots returns null single missing label`() {
		val groups = listOf(
			IngredientGroup(
				ingredients = listOf(
					RecipeIngredient(text = "Chicken breast"),
					RecipeIngredient(text = "Rice"),
				),
			),
		)

		missingIngredientCount(groups, isSlotCovered = { false }) shouldBe 2
		singleMissingIngredientLabel(groups, isSlotCovered = { false }) shouldBe null
	}

	@Test
	fun `fully covered recipe has zero missing ingredients`() {
		val groups = listOf(chickenTomatoGroup())

		missingIngredientCount(groups, isSlotCovered = { true }) shouldBe 0
		singleMissingIngredientLabel(groups, isSlotCovered = { true }) shouldBe null
	}

	@Test
	fun `required multi member slot is uncovered when any member is missing`() {
		val groups = listOf(
			IngredientGroup(
				ingredients = listOf(
					RecipeIngredient(text = "Chicken breast"),
					RecipeIngredient(text = "Tomato"),
				),
			),
		)

		missingIngredientCount(groups, isSlotCovered = { slot ->
			slot.all { ingredient -> ingredient.text != "Tomato" }
		}) shouldBe 1
		singleMissingIngredientLabel(groups, isSlotCovered = { slot ->
			slot.all { ingredient -> ingredient.text != "Tomato" }
		}) shouldBe "Tomato"
	}

	private fun chickenTomatoGroup(): IngredientGroup = IngredientGroup(
		ingredients = listOf(
			RecipeIngredient(text = "Chicken breast"),
			RecipeIngredient(text = "Tomato"),
			RecipeIngredient(text = "Salt"),
		),
	)
}

package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RecipePantryNearMissTest {

	private val recipeId = 1
	private val availableIngredients = listOf("Chicken", "Tomato", "Salt")

	@Test
	fun `returns missing ingredient label when exactly one slot is uncovered`() {
		singleMissingPantryIngredientLabel(
			recipeId = recipeId,
			availableIngredients = availableIngredients,
			loadIngredientGroups = { listOf(chickenTomatoBasilGroup()) },
		) shouldBe "Basil"
	}

	@Test
	fun `returns null when recipe is fully covered by pantry`() {
		singleMissingPantryIngredientLabel(
			recipeId = recipeId,
			availableIngredients = availableIngredients,
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe null
	}

	@Test
	fun `returns null when more than one slot is uncovered`() {
		singleMissingPantryIngredientLabel(
			recipeId = recipeId,
			availableIngredients = availableIngredients,
			loadIngredientGroups = { listOf(chickenRiceOnionGroup()) },
		) shouldBe null
	}

	@Test
	fun `default pantry staples do not count as missing`() {
		singleMissingPantryIngredientLabel(
			recipeId = recipeId,
			availableIngredients = listOf("Chicken", "Tomato"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe null
	}

	@Test
	fun `alternative slot missing returns combined label`() {
		val group = IngredientGroup(
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
		)

		singleMissingPantryIngredientLabel(
			recipeId = recipeId,
			availableIngredients = listOf("Tomato"),
			loadIngredientGroups = { listOf(group) },
		) shouldBe "Chicken breast or Tofu"
	}

	private fun chickenTomatoGroup(): IngredientGroup = IngredientGroup(
		ingredients = listOf(
			RecipeIngredient(text = "Chicken breast"),
			RecipeIngredient(text = "Tomato"),
			RecipeIngredient(text = "Salt"),
		),
	)

	private fun chickenTomatoBasilGroup(): IngredientGroup = IngredientGroup(
		ingredients = listOf(
			RecipeIngredient(text = "Chicken breast"),
			RecipeIngredient(text = "Tomato"),
			RecipeIngredient(text = "Basil"),
		),
	)

	private fun chickenRiceOnionGroup(): IngredientGroup = IngredientGroup(
		ingredients = listOf(
			RecipeIngredient(text = "Chicken breast"),
			RecipeIngredient(text = "Rice"),
			RecipeIngredient(text = "Onion"),
		),
	)
}

package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RecipeKeyIngredientMatchingTest {

	private val recipeId = 1

	@Test
	fun `empty key ingredients matches all recipes`() {
		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = emptyList(),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe true
	}

	@Test
	fun `single key ingredient matches when present in recipe`() {
		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Tomato"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe true
	}

	@Test
	fun `single key ingredient does not match when absent from recipe`() {
		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Basil"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe false
	}

	@Test
	fun `multiple key ingredients require all matches`() {
		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Chicken", "Tomato"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe true

		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Chicken", "Basil"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe false
	}

	@Test
	fun `key ingredient matches alternative ingredient slot`() {
		val group = IngredientGroup(
			ingredients = listOf(
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

		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Tofu"),
			loadIngredientGroups = { listOf(group) },
		) shouldBe true
	}

	@Test
	fun `optional ingredient lines do not satisfy key ingredients`() {
		val group = IngredientGroup(
			ingredients = listOf(
				RecipeIngredient(
					text = "optional Tomato, to garnish",
					requirement = IngredientRequirement.OPTIONAL,
				),
			),
		)

		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("Tomato"),
			loadIngredientGroups = { listOf(group) },
		) shouldBe false
	}

	@Test
	fun `key ingredient matching is case insensitive and partial`() {
		recipeContainsAllKeyIngredients(
			recipeId = recipeId,
			keyIngredients = listOf("chIck"),
			loadIngredientGroups = { listOf(chickenTomatoGroup()) },
		) shouldBe true
	}

	private fun chickenTomatoGroup(): IngredientGroup = IngredientGroup(
		ingredients = listOf(
			RecipeIngredient(text = "Chicken breast"),
			RecipeIngredient(text = "Tomato"),
			RecipeIngredient(text = "Salt"),
		),
	)
}

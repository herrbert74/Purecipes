package com.purecipes.feature.recipedetails.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeDetailsAccessorTest {

	@Test
	fun `details repository returns shared domain recipe details`() = runTest {
		val expected = RecipeDetails(
			id = 42,
			title = "Tomato Pasta",
			description = "Simple dinner.",
			imageUrl = "https://example.com/pasta.jpg",
			ingredientGroups = listOf(
				IngredientGroup(
					name = "Sauce",
					ingredients = listOf("2 tomatoes", "1 garlic clove"),
				)
			),
			steps = listOf("Boil pasta", "Make sauce"),
			totalTime = 25,
			yields = "2 servings",
			cuisine = Cuisine.ITALIAN,
		)
		val accessor = RecipeDetailsAccessor(
			RecipeDetailsRemoteDataSource(FakePurecipesApi(initialRecipeDetails = listOf(expected))),
		)

		val outcome = accessor.getRecipeDetails(42)

		assertEquals(expected, outcome.get())
		assertNull(outcome.getError())
	}
}

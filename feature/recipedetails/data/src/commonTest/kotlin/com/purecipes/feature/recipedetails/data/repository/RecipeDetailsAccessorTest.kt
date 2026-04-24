package com.purecipes.feature.recipedetails.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import com.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import com.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeDetailsAccessorTest {

	@Test
	fun `details repository returns shared domain recipe details`() = runTest {
		val expected = fakeRecipeDetails(steps = listOf("Boil pasta", "Make sauce"))
		val accessor = RecipeDetailsAccessor(
			RecipeDetailsRemoteDataSource(FakePurecipesApi(initialRecipeDetails = listOf(expected))),
		)

		val outcome = accessor.getRecipeDetails(42)

		assertEquals(expected, outcome.get())
		assertNull(outcome.getError())
	}
}

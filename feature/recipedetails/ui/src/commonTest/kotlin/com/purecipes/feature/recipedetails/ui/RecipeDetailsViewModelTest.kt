package com.purecipes.feature.recipedetails.ui

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsViewModelTest {

	@Test
	fun `details view model loads recipe details`() = runTest {
		val recipe = sampleRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals(recipe, viewModel.recipeDetails)
		assertNull(viewModel.errorMessage)
		assertFalse(viewModel.isLoading)
	}

	@Test
	fun `details view model exposes repository error`() = runTest {
		val repository = FakeRecipeDetailsRepository(Err(Failure.ServerError("Recipe failed")))
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals("Recipe failed", viewModel.errorMessage)
		assertNull(viewModel.recipeDetails)
		assertFalse(viewModel.isLoading)
	}

	private class FakeRecipeDetailsRepository(
		private val result: Outcome<RecipeDetails>,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
	}
}

internal fun sampleRecipeDetails(): RecipeDetails = RecipeDetails(
	id = 42,
	title = "Tomato Pasta",
	description = "Simple dinner.",
	imageUrl = "https://example.com/pasta.jpg",
	ingredientGroups = listOf(
		IngredientGroup(
			name = "Sauce",
			ingredients = listOf("2 tomatoes", "1 garlic clove"),
		),
	),
	steps = listOf("Boil pasta", "Make sauce", "Serve"),
	totalTime = 25,
	yields = "2 servings",
	cuisine = "Italian",
)

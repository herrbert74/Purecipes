package com.purecipes.feature.newrecipe.ui

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRecipeViewModelTest {

	@Test
	fun `loading recipes exposes the existing saved list`() = runTest {
		val recipe = sampleCreatedRecipe()
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe))
		val viewModel = CreateRecipeViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(repository),
			saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		assertEquals(listOf(recipe), viewModel.recipes.toList())
		assertFalse(viewModel.isLoading)
	}

	@Test
	fun `save validates required fields`() = runTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = CreateRecipeViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(repository),
			saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.saveRecipe()

		assertEquals("Add a recipe title.", viewModel.formErrorMessage)
		assertTrue(repository.savedRequests.isEmpty())
	}

	@Test
	fun `save adds a new recipe to the list`() = runTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = CreateRecipeViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(repository),
			saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.onTitleChange("Tomato Pasta")
		viewModel.onDescriptionChange("Quick weeknight dinner.")
		viewModel.onIngredientsChange("200 g pasta\n2 tomatoes")
		viewModel.onStepsChange("Boil the pasta\nFinish with the tomatoes")
		viewModel.saveRecipe()

		advanceUntilIdle()

		assertEquals(1, viewModel.recipes.size)
		assertEquals("Tomato Pasta", viewModel.recipes.single().title)
		assertEquals("Recipe uploaded.", viewModel.successMessage)
		assertNull(viewModel.formErrorMessage)
	}

	@Test
	fun `editing a recipe updates the existing item`() = runTest {
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(sampleCreatedRecipe()))
		val viewModel = CreateRecipeViewModel(
			getCreatedRecipes = GetCreatedRecipesUseCase(repository),
			saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()
		viewModel.editRecipe(viewModel.recipes.single())
		viewModel.onTitleChange("Creamy Tomato Pasta")
		viewModel.saveRecipe()

		advanceUntilIdle()

		assertEquals(1, viewModel.recipes.size)
		assertEquals("Creamy Tomato Pasta", viewModel.recipes.single().title)
		assertTrue(viewModel.isEditing)
	}

	private class FakeCreatedRecipeRepository(
		initialRecipes: List<RecipeDetails> = emptyList(),
	) : CreatedRecipeRepository {

		private val storedRecipes = initialRecipes.toMutableList()

		val savedRequests = mutableListOf<SaveCreatedRecipeRequest>()

		override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> = Ok(storedRecipes.toList())

		override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
			savedRequests += request
			val recipeId = request.recipeId ?: ((storedRecipes.minOfOrNull(RecipeDetails::id)?.takeIf { it < 0 } ?: 0) - 1)
			val recipe = RecipeDetails(
				id = recipeId,
				title = request.title,
				description = request.description,
				imageUrl = request.imageUrl,
				ingredientGroups = listOf(IngredientGroup(ingredients = request.ingredients)),
				steps = request.steps,
				totalTime = request.totalTime,
				yields = request.yields,
				cuisine = request.cuisine,
			)
			storedRecipes.removeAll { it.id == recipe.id }
			storedRecipes.add(index = 0, element = recipe)
			return Ok(recipe)
		}
	}
}

private fun sampleCreatedRecipe(): RecipeDetails = RecipeDetails(
	id = -1,
	title = "Tomato Pasta",
	description = "Quick weeknight dinner.",
	imageUrl = null,
	ingredientGroups = listOf(
		IngredientGroup(
			ingredients = listOf("200 g pasta", "2 tomatoes"),
		),
	),
	steps = listOf("Boil the pasta", "Finish with the tomatoes"),
	totalTime = 20,
	yields = "2 servings",
	cuisine = "Italian",
)

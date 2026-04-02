package com.purecipes.feature.cooking.ui

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StepByStepCookingViewModelTest {

	@Test
	fun stepByStepViewModelAdvancesAndClampsNavigation() = runTest {
		val recipe = sampleRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val viewModel = StepByStepCookingViewModel(
			recipeId = recipe.id,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		viewModel.previousStep()
		assertEquals(0, viewModel.currentStepIndex)

		viewModel.nextStep()
		viewModel.nextStep()
		viewModel.nextStep()

		assertEquals(recipe.steps.lastIndex, viewModel.currentStepIndex)

		viewModel.previousStep()
		assertEquals(recipe.steps.lastIndex - 1, viewModel.currentStepIndex)
	}

	@Test
	fun stepByStepViewModelSetsAndClampsCurrentPage() = runTest {
		val recipe = sampleRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val viewModel = StepByStepCookingViewModel(
			recipeId = recipe.id,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			coroutineScope = this,
		)

		advanceUntilIdle()

		viewModel.setCurrentStep(1)
		assertEquals(1, viewModel.currentStepIndex)

		viewModel.setCurrentStep(99)
		assertEquals(recipe.steps.lastIndex, viewModel.currentStepIndex)

		viewModel.setCurrentStep(-1)
		assertEquals(0, viewModel.currentStepIndex)
	}

	private class FakeRecipeDetailsRepository(
		private val result: Outcome<RecipeDetails>,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
	}
}

private fun sampleRecipeDetails(): RecipeDetails = RecipeDetails(
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
	cuisine = Cuisine.ITALIAN,
)

package com.purecipes.feature.recipedetails.ui

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.RecipeDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StepByStepCookingViewModelTest {

	@Test
	fun `step by step view model advances and clamps navigation`() = runTest {
		val recipe = sampleRecipeDetails()
		val viewModel = StepByStepCookingViewModel(
			recipeId = recipe.id,
			repository = FakeRecipeDetailsRepository(Ok(recipe)),
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

	private class FakeRecipeDetailsRepository(
		private val result: Outcome<RecipeDetails>,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
	}
}

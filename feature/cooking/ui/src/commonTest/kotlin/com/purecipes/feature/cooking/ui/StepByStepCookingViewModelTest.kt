package com.purecipes.feature.cooking.ui

import com.github.michaelbull.result.Ok
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.repository.MeasurementPreferencesRepository
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.MeasurementPreferences
import com.purecipes.shared.domain.model.MeasurementSystem
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = StepByStepCookingViewModel(
			recipeId = recipe.id,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
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
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = StepByStepCookingViewModel(
			recipeId = recipe.id,
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
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

	private class FakeMeasurementPreferencesRepository(
		private val defaults: MeasurementPreferences = MeasurementPreferences(
			preferredSystem = MeasurementSystem.METRIC,
		),
	) : MeasurementPreferencesRepository {

		private val flow = MutableStateFlow(defaults)

		override fun observeMeasurementPreferences(): Flow<MeasurementPreferences> = flow

		override suspend fun getMeasurementPreferences(): MeasurementPreferences = flow.value

		override suspend fun saveMeasurementPreferences(preferences: MeasurementPreferences) {
			flow.value = preferences
		}

		override suspend fun resetMeasurementPreferences() {
			flow.value = defaults
		}

		override suspend fun markMismatchNotificationSeen(recipeId: Int) = Unit
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

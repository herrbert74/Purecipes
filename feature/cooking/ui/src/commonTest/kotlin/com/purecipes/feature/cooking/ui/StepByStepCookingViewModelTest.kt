package com.purecipes.feature.cooking.ui

import com.github.michaelbull.result.Ok
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import com.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import com.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import com.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StepByStepCookingViewModelTest {

	@Test
	fun stepByStepViewModelAdvancesAndClampsNavigation() = runTest {
		val recipe = fakeRecipeDetails()
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
		val recipe = fakeRecipeDetails()
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

}

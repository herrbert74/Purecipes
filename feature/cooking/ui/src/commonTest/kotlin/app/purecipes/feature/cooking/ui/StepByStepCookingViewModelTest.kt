package app.purecipes.feature.cooking.ui

import com.github.michaelbull.result.Ok
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StepByStepCookingViewModelTest {

	@Test
	fun `step by step view model advances and clamps navigation`() = runTest {
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
		viewModel.currentStepIndex shouldBe 0

		viewModel.nextStep()
		viewModel.nextStep()
		viewModel.nextStep()

		viewModel.currentStepIndex shouldBe recipe.steps.lastIndex

		viewModel.previousStep()
		viewModel.currentStepIndex shouldBe recipe.steps.lastIndex - 1
	}

	@Test
	fun `step by step view model sets and clamps current page`() = runTest {
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
		viewModel.currentStepIndex shouldBe 1

		viewModel.setCurrentStep(99)
		viewModel.currentStepIndex shouldBe recipe.steps.lastIndex

		viewModel.setCurrentStep(-1)
		viewModel.currentStepIndex shouldBe 0
	}

}

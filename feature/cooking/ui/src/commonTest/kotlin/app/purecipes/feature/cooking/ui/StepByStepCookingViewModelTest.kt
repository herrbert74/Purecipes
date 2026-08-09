package app.purecipes.feature.cooking.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StepByStepCookingViewModelTest {

	@Test
	fun `step by step view model advances and clamps navigation`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val viewModel = createViewModel(recipeId = recipe.id, recipe = recipe)

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
	fun `step by step view model sets and clamps current page`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val viewModel = createViewModel(recipeId = recipe.id, recipe = recipe)

		advanceUntilIdle()

		viewModel.setCurrentStep(1)
		viewModel.currentStepIndex shouldBe 1

		viewModel.setCurrentStep(99)
		viewModel.currentStepIndex shouldBe recipe.steps.lastIndex

		viewModel.setCurrentStep(-1)
		viewModel.currentStepIndex shouldBe 0
	}

	@Test
	fun `loading recipe tracks cooking started`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			analyticsRepository = analyticsRepository,
			crashRepository = crashRepository,
		)

		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingStarted>() shouldBe listOf(
			AnalyticsEvent.CookingStarted(
				recipeId = recipe.id,
				recipeName = recipe.title,
				origin = AnalyticsOrigin.RECIPE_DETAILS,
				stepCount = recipe.steps.size,
			),
		)
		crashRepository.breadcrumbs shouldBe listOf(CrashBreadcrumb.cookingStarted(recipe.id))
	}

	@Test
	fun `advancing steps tracks cooking step viewed and completed`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val analyticsRepository = FakeAnalyticsRepository()
		val crashRepository = FakeCrashRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			analyticsRepository = analyticsRepository,
			crashRepository = crashRepository,
		)

		advanceUntilIdle()
		viewModel.nextStep()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingStepViewed>() shouldBe listOf(
			AnalyticsEvent.CookingStepViewed(
				recipeId = recipe.id,
				recipeName = recipe.title,
				stepIndex = 1,
				stepCount = recipe.steps.size,
			),
		)
		crashRepository.breadcrumbs shouldBe listOf(
			CrashBreadcrumb.cookingStarted(recipe.id),
			CrashBreadcrumb.cookingStepAdvanced(recipe.id, 1),
		)

		repeat(recipe.steps.lastIndex - 1) {
			viewModel.nextStep()
		}
		advanceUntilIdle()

		val completedEvents = analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingCompleted>()
		completedEvents.size shouldBe 1
		completedEvents.single() shouldBe AnalyticsEvent.CookingCompleted(
			recipeId = recipe.id,
			recipeName = recipe.title,
			durationSeconds = completedEvents.single().durationSeconds,
			stepCount = recipe.steps.size,
			origin = AnalyticsOrigin.RECIPE_DETAILS,
		)
	}

	@Test
	fun `leaving cooking before completion tracks cooking abandoned`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()
		viewModel.nextStep()
		advanceUntilIdle()
		viewModel.trackCookingAbandonedIfNeeded()

		val abandonedEvents = analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingAbandoned>()
		abandonedEvents.size shouldBe 1
		abandonedEvents.single() shouldBe AnalyticsEvent.CookingAbandoned(
			recipeId = recipe.id,
			recipeName = recipe.title,
			lastStepIndex = 1,
			stepCount = recipe.steps.size,
			durationSeconds = abandonedEvents.single().durationSeconds,
		)
	}

	@Test
	fun `cooking abandoned is not tracked after completion`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()
		repeat(recipe.steps.lastIndex) {
			viewModel.nextStep()
		}
		advanceUntilIdle()
		viewModel.trackCookingAbandonedIfNeeded()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingAbandoned>() shouldBe emptyList()
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingCompleted>().size shouldBe 1
	}

	@Test
	fun `recipe load failure tracks recipe load failed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		createViewModel(
			recipeId = 42,
			recipeDetailsRepository = FakeRecipeDetailsRepository(Err(Failure.ServerError("boom"))),
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()

		analyticsRepository.trackedEvents.single().shouldBeInstanceOf<AnalyticsEvent.RecipeLoadFailed>()
		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.RecipeLoadFailed(
			recipeId = 42,
			errorKind = AnalyticsErrorKind.SERVER_ERROR,
		)
	}

	private fun createViewModel(
		recipeId: Int,
		recipe: RecipeDetails = fakeRecipeDetails(id = recipeId),
		recipeDetailsRepository: FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(Ok(recipe)),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		crashRepository: FakeCrashRepository = FakeCrashRepository(),
	): StepByStepCookingViewModel {
		val measurementRepository = FakeMeasurementPreferencesRepository()
		return StepByStepCookingViewModel(
			getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
			sendHandledException = SendHandledExceptionUseCase(crashRepository),
			recipeId = recipeId,
		)
	}
}

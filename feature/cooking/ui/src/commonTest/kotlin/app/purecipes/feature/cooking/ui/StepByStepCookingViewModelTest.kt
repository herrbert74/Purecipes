package app.purecipes.feature.cooking.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsShareType
import app.purecipes.feature.analytics.domain.model.CrashBreadcrumb
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
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
		viewModel.currentPageIndex shouldBe 0

		viewModel.nextStep()
		viewModel.nextStep()
		viewModel.nextStep()

		viewModel.currentPageIndex shouldBe recipe.steps.size

		viewModel.previousStep()
		viewModel.currentPageIndex shouldBe recipe.steps.lastIndex
	}

	@Test
	fun `step by step view model sets and clamps current page`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val viewModel = createViewModel(recipeId = recipe.id, recipe = recipe)

		advanceUntilIdle()

		viewModel.setCurrentPage(1)
		viewModel.currentPageIndex shouldBe 1

		viewModel.setCurrentPage(99)
		viewModel.currentPageIndex shouldBe recipe.steps.size

		viewModel.setCurrentPage(-1)
		viewModel.currentPageIndex shouldBe 0
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
				isPrivate = recipe.isPrivate,
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
				isPrivate = recipe.isPrivate,
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
			isPrivate = recipe.isPrivate,
		)
	}

	@Test
	fun `moving to finish page does not track an extra cooking step viewed`() = runViewModelTest {
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
		val stepViewedCount =
			analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingStepViewed>().size

		viewModel.nextStep()
		advanceUntilIdle()

		viewModel.currentPageIndex shouldBe recipe.steps.size
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingStepViewed>().size shouldBe
			stepViewedCount
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookingCompleted>().size shouldBe 1
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
			isPrivate = recipe.isPrivate,
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

	@Test
	fun `toggle favorite updates recipe state and tracks event`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val favoritesRepository = FakeFavoritesRepository()
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			favoritesRepository = favoritesRepository,
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()
		advanceUntilIdle()

		viewModel.recipeDetails?.isFavorite shouldBe true
		viewModel.favoriteErrorMessage shouldBe null
		favoritesRepository.addedRecipeIds shouldBe listOf(recipe.id)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.FavoriteChanged>() shouldBe listOf(
			AnalyticsEvent.FavoriteChanged(
				recipeId = recipe.id,
				recipeName = recipe.title,
				isFavorite = true,
				origin = AnalyticsOrigin.COOKING,
				isPrivate = recipe.isPrivate,
			),
		)
	}

	@Test
	fun `favorite added event marks matching recipe as favorite`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val favoritesRepository = FakeFavoritesRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			favoritesRepository = favoritesRepository,
			sessionKey = "session",
		)

		advanceUntilIdle()
		viewModel.recipeDetails?.isFavorite shouldBe false

		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Added(recipe.id))
		advanceUntilIdle()

		viewModel.recipeDetails?.isFavorite shouldBe true
	}

	@Test
	fun `share current recipe tracks recipe shared`() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()
		viewModel.shareCurrentRecipe()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeShared>() shouldBe listOf(
			AnalyticsEvent.RecipeShared(
				recipeId = recipe.id,
				recipeName = recipe.title,
				origin = AnalyticsOrigin.COOKING,
				isPrivate = recipe.isPrivate,
				shareType = AnalyticsShareType.RECIPE,
			),
		)
	}

	@Test
	fun `create cookbook and add rejects duplicate name`() = runViewModelTest {
		val cookbooksRepository = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(
						CookbookSummary(
							id = 5,
							name = "Weeknight Dinners",
							recipeCount = 0,
							updatedAtEpochMillis = 0L,
						),
					),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = createViewModel(
			recipeId = 42,
			cookbooksRepository = cookbooksRepository,
		)
		advanceUntilIdle()
		viewModel.prepareCookbookPicker()
		advanceUntilIdle()

		var returnedError: String? = null
		viewModel.createCookbookAndAdd("  weeknight dinners ") { err ->
			returnedError = err
		}
		advanceUntilIdle()

		returnedError shouldBe "Cookbook already exists"
		viewModel.cookbookActionError shouldBe "Cookbook already exists"
		cookbooksRepository.createCookbookCallCount shouldBe 0
		cookbooksRepository.addRecipeToCookbookCallCount shouldBe 0
	}

	@Test
	fun `create cookbook and add tracks cookbook created and recipe added`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val createdCookbook = CookbookSummary(
			id = 15,
			name = "Batch Cooking",
			recipeCount = 0,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(
			createCookbookResult = Ok(createdCookbook),
		)
		val recipe = fakeRecipeDetails()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			cookbooksRepository = cookbooksRepository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()

		viewModel.createCookbookAndAdd("Batch Cooking") { err ->
			err shouldBe null
		}
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookCreated>() shouldBe listOf(
			AnalyticsEvent.CookbookCreated(
				cookbookId = createdCookbook.id,
				cookbookName = createdCookbook.name,
			),
		)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeAddedToCookbook>() shouldBe listOf(
			AnalyticsEvent.RecipeAddedToCookbook(
				recipeId = recipe.id,
				recipeName = recipe.title,
				cookbookId = createdCookbook.id,
				cookbookName = createdCookbook.name,
				origin = AnalyticsOrigin.COOKING,
				isPrivate = recipe.isPrivate,
			),
		)
	}

	@Test
	fun `add recipe to cookbook tracks recipe added with cooking origin`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val cookbook = CookbookSummary(
			id = 8,
			name = "Sunday Roast",
			recipeCount = 2,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(cookbook),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val recipe = fakeRecipeDetails()
		val viewModel = createViewModel(
			recipeId = recipe.id,
			recipe = recipe,
			cookbooksRepository = cookbooksRepository,
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()
		viewModel.prepareCookbookPicker()
		advanceUntilIdle()

		viewModel.addRecipeToCookbookId(cookbook.id) { err ->
			err shouldBe null
		}
		advanceUntilIdle()

		cookbooksRepository.addRecipeToCookbookCallCount shouldBe 1
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeAddedToCookbook>() shouldBe listOf(
			AnalyticsEvent.RecipeAddedToCookbook(
				recipeId = recipe.id,
				recipeName = recipe.title,
				cookbookId = cookbook.id,
				cookbookName = cookbook.name,
				origin = AnalyticsOrigin.COOKING,
				isPrivate = recipe.isPrivate,
			),
		)
	}

	private fun createViewModel(
		recipeId: Int,
		recipe: RecipeDetails = fakeRecipeDetails(id = recipeId),
		recipeDetailsRepository: FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(Ok(recipe)),
		favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
		cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		crashRepository: FakeCrashRepository = FakeCrashRepository(),
		sessionKey: String? = null,
	): StepByStepCookingViewModel {
		val measurementRepository = FakeMeasurementPreferencesRepository()
		return StepByStepCookingViewModel(
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
			shareRecipe = ShareRecipeUseCase(
				object : ShareRepository {
					override fun shareText(text: String, title: String?) = Unit
				},
			),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
			sendHandledException = SendHandledExceptionUseCase(crashRepository),
			recipeId = recipeId,
			sessionKey = sessionKey,
		)
	}
}

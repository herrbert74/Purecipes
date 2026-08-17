package app.purecipes.feature.recipedetails.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsShareType
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.repository.FavoritesRepository
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.MeasurementPreferences
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeFormatHandling
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailsViewModelTest {

	private val fakeCookbooksRepository = FakeCookbooksRepository()
	private val shareRecipe = ShareRecipeUseCase(
		object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		},
	)

	@Test
	fun `measurement preference changes reprocess loaded recipe`() = runViewModelTest {
		val recipe = fakeRecipeDetails(
			ingredientGroups = listOf(
				IngredientGroup(
					ingredients = recipeIngredients("2 cups flour"),
				),
			),
			measurementSystem = MeasurementSystem.IMPERIAL,
		)
		val measurementRepository = FakeMeasurementPreferencesRepository(
			defaults = MeasurementPreferences(
				preferredSystem = MeasurementSystem.METRIC,
				formatHandling = RecipeFormatHandling.KEEP_AS_IS,
			),
		)
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(FakeRecipeDetailsRepository(recipe)),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.recipeDetails?.ingredientGroups?.single()?.ingredients?.single()?.text shouldBe "2 cups flour"

		measurementRepository.saveMeasurementPreferences(
			MeasurementPreferences(
				preferredSystem = MeasurementSystem.METRIC,
				formatHandling = RecipeFormatHandling.CONVERT_TO_PREFERRED,
			),
		)
		advanceUntilIdle()

		viewModel.isRecipeConverted shouldBe true
		viewModel.recipeDetails?.ingredientGroups?.single()?.ingredients?.single()?.text.orEmpty() shouldContain "mL"
	}

	@Test
	fun detailsViewModelLoadsRecipeDetails() = runViewModelTest {
		val recipe = fakeRecipeDetails()
		val repository = FakeRecipeDetailsRepository(Ok(recipe))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()

		viewModel.recipeDetails shouldBe recipe
		viewModel.errorMessage shouldBe null
		viewModel.isLoading shouldBe false
	}

	@Test
	fun `signing in reloads session-specific recipe details`() = runViewModelTest {
		val signedOutRecipe = fakeRecipeDetails()
		val repository = MutableRecipeDetailsRepository(Ok(signedOutRecipe))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = signedOutRecipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)
		advanceUntilIdle()
		viewModel.recipeDetails?.isFavorite shouldBe false

		repository.result = Ok(signedOutRecipe.copy(isFavorite = true))
		viewModel.onSessionKeyChanged("session")
		advanceUntilIdle()

		viewModel.recipeDetails?.isFavorite shouldBe true
	}

	@Test
	fun `details view model exposes repository error`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = FakeRecipeDetailsRepository(Err(Failure.ServerError("Recipe failed")))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()

		viewModel.errorMessage shouldBe "Recipe failed"
		viewModel.recipeDetails shouldBe null
		viewModel.isLoading shouldBe false
		analyticsRepository.trackedEvents shouldBe listOf(
			AnalyticsEvent.RecipeLoadFailed(
				recipeId = 42,
				errorKind = AnalyticsErrorKind.SERVER_ERROR,
			),
		)
	}

	@Test
	fun `share current recipe tracks recipe shared`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val recipe = fakeRecipeDetails()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(FakeRecipeDetailsRepository(Ok(recipe))),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.shareCurrentRecipe()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeShared>() shouldBe listOf(
			AnalyticsEvent.RecipeShared(
				recipeId = recipe.id,
				recipeName = recipe.title,
				origin = AnalyticsOrigin.SEARCH,
				isPrivate = recipe.isPrivate,
				shareType = AnalyticsShareType.RECIPE,
			),
		)
	}

	@Test
	fun `share current recipe is skipped for private recipes`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val recipe = fakeRecipeDetails().copy(isPrivate = true)
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(FakeRecipeDetailsRepository(Ok(recipe))),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.shareCurrentRecipe()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeShared>() shouldBe emptyList()
	}

	@Test
	fun `toggle favorite updates recipe state`() = runViewModelTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val favoritesRepository = FakeFavoritesRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()
		advanceUntilIdle()

		true shouldBe viewModel.recipeDetails?.isFavorite
		viewModel.favoriteErrorMessage shouldBe null
	}

	@Test
	fun `favorite removed event clears favorite on matching recipe`() = runViewModelTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails().copy(isFavorite = true)))
		val favoritesRepository = FakeFavoritesRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = "session",
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.recipeDetails?.isFavorite shouldBe true

		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Removed(42))
		advanceUntilIdle()

		viewModel.recipeDetails?.isFavorite shouldBe false
	}

	@Test
	fun `favorite added event marks matching recipe as favorite`() = runViewModelTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val favoritesRepository = FakeFavoritesRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = "session",
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.recipeDetails?.isFavorite shouldBe false

		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Added(42))
		advanceUntilIdle()

		viewModel.recipeDetails?.isFavorite shouldBe true
	}

	@Test
	fun `toggle favorite marks updating synchronously`() = runViewModelTest {
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val favoriteStarted = CompletableDeferred<Unit>()
		val finishFavorite = CompletableDeferred<Unit>()
		val favoritesRepository = BlockingFavoritesRepository(favoriteStarted, finishFavorite)
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = null,
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(fakeCookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(fakeCookbooksRepository),
			createCookbook = CreateCookbookUseCase(fakeCookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(fakeCookbooksRepository),
			shareRecipe = shareRecipe,
		)

		advanceUntilIdle()
		viewModel.toggleFavorite()

		viewModel.isFavoriteUpdating shouldBe true
		viewModel.favoriteErrorMessage shouldBe null
		favoriteStarted.isCompleted shouldBe false

		advanceUntilIdle()
		favoriteStarted.isCompleted shouldBe true
		viewModel.isFavoriteUpdating shouldBe true

		finishFavorite.complete(Unit)
		advanceUntilIdle()

		viewModel.isFavoriteUpdating shouldBe false
		true shouldBe viewModel.recipeDetails?.isFavorite
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
		val repository = FakeRecipeDetailsRepository(Ok(fakeRecipeDetails()))
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = 42,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(repository),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = "session",
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(cookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
			shareRecipe = shareRecipe,
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
		val measurementRepository = FakeMeasurementPreferencesRepository()
		val viewModel = RecipeDetailsViewModel(
			recipeId = recipe.id,
			addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
			getRecipeDetails = GetRecipeDetailsUseCase(FakeRecipeDetailsRepository(Ok(recipe))),
			observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
			markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
			processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
			removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			trackEvent = TrackEventUseCase(analyticsRepository),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			sessionKey = "session",
			origin = AnalyticsOrigin.SEARCH.value,
			getRecipeCookbooks = GetRecipeCookbooksUseCase(cookbooksRepository),
			getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
			createCookbook = CreateCookbookUseCase(cookbooksRepository),
			addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
			shareRecipe = shareRecipe,
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
				origin = AnalyticsOrigin.RECIPE_DETAILS,
				isPrivate = recipe.isPrivate,
			),
		)
	}

	private class BlockingFavoritesRepository(
		private val favoriteStarted: CompletableDeferred<Unit>,
		private val finishFavorite: CompletableDeferred<Unit>,
	) : FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> {
			favoriteStarted.complete(Unit)
			finishFavorite.await()
			return Ok(Unit)
		}

		override suspend fun getFavoriteRecipesPage(pageNumber: Int, pageSize: Int) = Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)

		override fun observeFavoriteEvents() = emptyFlow<FavoriteEvent>()
	}

	private class MutableRecipeDetailsRepository(
		var result: Outcome<RecipeDetails>,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
	}

}

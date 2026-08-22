package app.purecipes.feature.library.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.shared.domain.model.CookbookListPage
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.random.Random
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

	private val getCookbookCoverImageUrl =
		GetCookbookCoverImageUrlUseCase(
			repository = object : CookbookCoverRepository {
				override fun getCookbookCoverImageUrl(
					cookbookId: Int,
					candidateImageUrls: List<String>,
					nowMillis: Long,
					random: Random,
				): String? = candidateImageUrls.firstOrNull()
			},
		)

	@Test
	fun `load favorites populates recipes`() = runViewModelTest {
		val expected = listOf(
			RecipeSummary(
				id = 42,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 25,
				isFavorite = true,
			),
		)
		val favoritesRepo = FakeFavoritesRepository(
			getFavoriteRecipesPageResult = Ok(
				SearchResultsPage(
					items = expected,
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = LibraryViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
			getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
			createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
			deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepo),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = "session",
		)

		viewModel.loadLibrary()
		advanceUntilIdle()

		kotlin.test.assertEquals(expected, viewModel.savedRecipes.toList())
		kotlin.test.assertEquals(null, viewModel.savedErrorMessage)
	}

	@Test
	fun `load favorites exposes error`() = runViewModelTest {
		val favoritesRepo = FakeFavoritesRepository(
			getFavoriteRecipesPageResult = Err(Failure.ServerError("Favorites failed")),
		)
		val viewModel = LibraryViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
			getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
			createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
			deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepo),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = "session",
		)

		viewModel.loadLibrary()
		advanceUntilIdle()

		kotlin.test.assertEquals("Favorites failed", viewModel.savedErrorMessage)
		kotlin.test.assertEquals(emptyList(), viewModel.savedRecipes.toList())
	}

	@Test
	fun `favorite event reloads saved recipes`() = runViewModelTest {
		val recipe = RecipeSummary(
			id = 42,
			title = "Tomato Pasta",
			cuisine = Cuisine.ITALIAN,
			imageUrl = null,
			totalTime = 25,
			isFavorite = true,
		)
		val favoritesRepo = FakeFavoritesRepository(
			getFavoriteRecipesPageResult = Ok(
				SearchResultsPage(
					items = listOf(recipe),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = LibraryViewModel(
			getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepo),
			getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
			createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
			deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
			getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
			getCookbookCoverImageUrl = getCookbookCoverImageUrl,
			importCookbookShare = unusedImportCookbookShareUseCase(),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepo),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			sessionKey = "session",
		)

		viewModel.loadLibrary()
		advanceUntilIdle()
		kotlin.test.assertEquals(listOf(recipe), viewModel.savedRecipes.toList())

		favoritesRepo.getFavoriteRecipesPageResult = Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = 1,
				pageSize = 20,
				totalMatches = 0,
			),
		)
		favoritesRepo.emitFavoriteEvent(FavoriteEvent.Removed(recipeId = recipe.id))
		advanceUntilIdle()

		kotlin.test.assertEquals(emptyList(), viewModel.savedRecipes.toList())
		kotlin.test.assertEquals(0, viewModel.totalSavedMatches)
	}

	@Test
	fun `signing in loads favorites and observes subsequent favorite events`() = runViewModelTest {
		val recipe = RecipeSummary(
			id = 42,
			title = "Tomato Pasta",
			cuisine = Cuisine.ITALIAN,
			imageUrl = null,
			totalTime = 25,
			isFavorite = true,
		)
		val favoritesRepo = FakeFavoritesRepository(
			getFavoriteRecipesPageResult = Ok(
				SearchResultsPage(
					items = listOf(recipe),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val viewModel = favoritesViewModel(
			favoritesRepository = favoritesRepo,
			sessionKey = null,
		)

		viewModel.onSessionKeyChanged("session")
		viewModel.loadLibrary()
		advanceUntilIdle()

		kotlin.test.assertEquals(listOf(recipe), viewModel.savedRecipes.toList())

		favoritesRepo.getFavoriteRecipesPageResult = Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = 1,
				pageSize = 20,
				totalMatches = 0,
			),
		)
		favoritesRepo.emitFavoriteEvent(FavoriteEvent.Removed(recipeId = recipe.id))
		advanceUntilIdle()

		kotlin.test.assertEquals(emptyList(), viewModel.savedRecipes.toList())
		kotlin.test.assertEquals(0, viewModel.totalSavedMatches)
	}

	@Test
	fun `create cookbook from name rejects duplicate name`() = runViewModelTest {
		val existingCookbook = CookbookSummary(
			id = 10,
			name = "Weeknight Dinners",
			recipeCount = 0,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(
			cookbooksPageResult = Ok(
				CookbookListPage(
					items = listOf(existingCookbook),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 1,
				),
			),
		)
		val favoritesRepo = FakeFavoritesRepository()
		val viewModel = favoritesViewModel(
			favoritesRepository = favoritesRepo,
			cookbooksRepository = cookbooksRepository,
		)
		viewModel.loadLibrary()
		advanceUntilIdle()

		var created = true
		viewModel.createCookbookFromName("  weeknight dinners ") { ok ->
			created = ok
		}
		advanceUntilIdle()

		kotlin.test.assertEquals(false, created)
		kotlin.test.assertEquals("Cookbook already exists", viewModel.createCookbookError)
		kotlin.test.assertEquals(0, cookbooksRepository.createCookbookCallCount)
	}

	@Test
	fun `delete cookbook rejects non-empty cookbook`() = runViewModelTest {
		val cookbooksRepository = FakeCookbooksRepository()
		val viewModel = favoritesViewModel(cookbooksRepository = cookbooksRepository)
		var deleted = true

		viewModel.deleteCookbook(
			CookbookSummary(
				id = 10,
				name = "Weeknight Dinners",
				recipeCount = 1,
				updatedAtEpochMillis = 0L,
			),
		) { ok ->
			deleted = ok
		}

		kotlin.test.assertEquals(false, deleted)
		kotlin.test.assertEquals("Only empty cookbooks can be deleted", viewModel.deleteCookbookError)
		kotlin.test.assertEquals(0, cookbooksRepository.deleteCookbookCallCount)
	}

	@Test
	fun `delete cookbook succeeds for empty cookbook`() = runViewModelTest {
		val cookbooksRepository = FakeCookbooksRepository()
		val viewModel = favoritesViewModel(cookbooksRepository = cookbooksRepository)
		var deleted = false

		viewModel.deleteCookbook(
			CookbookSummary(
				id = 11,
				name = "Empty Cookbook",
				recipeCount = 0,
				updatedAtEpochMillis = 0L,
			),
		) { ok ->
			deleted = ok
		}
		advanceUntilIdle()

		kotlin.test.assertEquals(true, deleted)
		kotlin.test.assertEquals(null, viewModel.deleteCookbookError)
		kotlin.test.assertEquals(1, cookbooksRepository.deleteCookbookCallCount)
	}

	private fun favoritesViewModel(
		favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
		cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
		sessionKey: String? = "session",
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		importCookbookShare: ImportCookbookShareUseCase = unusedImportCookbookShareUseCase(),
	): LibraryViewModel = LibraryViewModel(
		getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(favoritesRepository),
		getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
		createCookbook = CreateCookbookUseCase(cookbooksRepository),
		deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
		getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
		getCookbookCoverImageUrl = getCookbookCoverImageUrl,
		importCookbookShare = importCookbookShare,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
		sessionKey = sessionKey,
	)
}

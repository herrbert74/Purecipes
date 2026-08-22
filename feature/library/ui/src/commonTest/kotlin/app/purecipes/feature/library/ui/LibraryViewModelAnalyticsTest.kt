package app.purecipes.feature.library.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsFavoritesTab
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.random.Random
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelAnalyticsTest {

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
	fun `onTabSelected tracks favorites tab selected`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = favoritesViewModel(analyticsRepository = analyticsRepository)

		viewModel.onTabSelected(LibraryTab.Cookbooks)

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.FavoritesTabSelected(
			tab = AnalyticsFavoritesTab.COOKBOOKS,
		)
	}

	@Test
	fun `create cookbook tracks cookbook created`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val created = CookbookSummary(
			id = 22,
			name = "Meal Prep",
			recipeCount = 0,
			updatedAtEpochMillis = 0L,
		)
		val cookbooksRepository = FakeCookbooksRepository(createCookbookResult = Ok(created))
		val viewModel = favoritesViewModel(
			cookbooksRepository = cookbooksRepository,
			analyticsRepository = analyticsRepository,
		)

		viewModel.createCookbookFromName("Meal Prep") {}
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookCreated>().single() shouldBe
			AnalyticsEvent.CookbookCreated(
				cookbookId = created.id,
				cookbookName = created.name,
			)
	}

	@Test
	fun `delete cookbook tracks cookbook deleted`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = favoritesViewModel(analyticsRepository = analyticsRepository)

		viewModel.deleteCookbook(
			CookbookSummary(
				id = 11,
				name = "Empty Cookbook",
				recipeCount = 0,
				updatedAtEpochMillis = 0L,
			),
		) {}
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookDeleted>().single() shouldBe
			AnalyticsEvent.CookbookDeleted(cookbookId = 11)
	}

	@Test
	fun `import shared cookbook tracks completed and invokes callback`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val importedCookbook = CookbookSummary(
			id = 33,
			name = "Imported",
			recipeCount = 2,
			updatedAtEpochMillis = 0L,
		)
		val importUseCase = ImportCookbookShareUseCase(
			object : CookbookShareRepository {
				override suspend fun createShare(cookbookId: Int) =
					Err(Failure.ServerError("unused"))

				override suspend fun importShare(token: String) = Ok(
					CookbookImportResult(
						cookbook = importedCookbook,
						recipesImported = 2,
						recipesSkipped = 0,
						alreadyImported = false,
					),
				)
			},
		)
		val viewModel = favoritesViewModel(
			analyticsRepository = analyticsRepository,
			importCookbookShare = importUseCase,
		)
		var importedCookbookId: Int? = null

		viewModel.importSharedCookbook("share-token") { cookbookId, _, _ ->
			importedCookbookId = cookbookId
		}
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookImportCompleted>().single() shouldBe
			AnalyticsEvent.CookbookImportCompleted(
				importedRecipeCount = 2,
				cookbookId = importedCookbook.id,
			)
		importedCookbookId shouldBe importedCookbook.id
	}

	@Test
	fun `import shared cookbook tracks failed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val importUseCase = ImportCookbookShareUseCase(
			object : CookbookShareRepository {
				override suspend fun createShare(cookbookId: Int) =
					Err(Failure.ServerError("unused"))

				override suspend fun importShare(token: String) =
					Err(Failure.ServerError("Import failed"))
			},
		)
		val viewModel = favoritesViewModel(
			analyticsRepository = analyticsRepository,
			importCookbookShare = importUseCase,
		)

		viewModel.importSharedCookbook("share-token")
		advanceUntilIdle()

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.CookbookImportFailed(
			errorKind = AnalyticsErrorKind.SERVER_ERROR,
		)
	}

	private fun favoritesViewModel(
		cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		importCookbookShare: ImportCookbookShareUseCase = unusedImportCookbookShareUseCase(),
	): LibraryViewModel = LibraryViewModel(
		getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
		getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
		createCookbook = CreateCookbookUseCase(cookbooksRepository),
		deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
		getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
		getCookbookCoverImageUrl = getCookbookCoverImageUrl,
		importCookbookShare = importCookbookShare,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		trackEvent = TrackEventUseCase(analyticsRepository),
		sessionKey = "session",
	)
}

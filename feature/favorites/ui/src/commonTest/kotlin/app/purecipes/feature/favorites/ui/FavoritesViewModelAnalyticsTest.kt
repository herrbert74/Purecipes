package app.purecipes.feature.favorites.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsErrorKind
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsFavoritesTab
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.CreateCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookShareToken
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
class FavoritesViewModelAnalyticsTest {

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

		viewModel.onTabSelected(FavoritesTab.Cookbooks)

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
	fun `open cookbook detail tracks cookbook opened`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = favoritesViewModel(analyticsRepository = analyticsRepository)

		viewModel.openCookbookDetail(
			cookbookId = 10,
			name = "Weeknight Dinners",
			recipeCount = 4,
		)
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookOpened>().single() shouldBe
			AnalyticsEvent.CookbookOpened(
				cookbookId = 10,
				cookbookName = "Weeknight Dinners",
				recipeCount = 4,
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
	fun `share open cookbook tracks cookbook shared`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val shareRepository = object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		}
		val cookbookShareRepository = object : CookbookShareRepository {
			override suspend fun createShare(cookbookId: Int) = Ok(CookbookShareToken(token = "token"))

			override suspend fun importShare(token: String) = Err(Failure.ServerError("unused"))
		}
		val viewModel = favoritesViewModel(
			analyticsRepository = analyticsRepository,
			shareCookbook = ShareCookbookUseCase(
				createCookbookShareUseCase = CreateCookbookShareUseCase(cookbookShareRepository),
				shareRepository = shareRepository,
			),
		)
		viewModel.openCookbookDetail(cookbookId = 10, name = "Weeknight Dinners", recipeCount = 2)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.shareOpenCookbook()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookShared>().single() shouldBe
			AnalyticsEvent.CookbookShared(
				cookbookId = 10,
				cookbookName = "Weeknight Dinners",
				origin = AnalyticsOrigin.FAVORITES,
			)
	}

	@Test
	fun `import shared cookbook tracks completed and opened`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val importedCookbook = CookbookSummary(
			id = 33,
			name = "Imported",
			recipeCount = 2,
			updatedAtEpochMillis = 0L,
		)
		val importUseCase = ImportCookbookShareUseCase(
			object : CookbookShareRepository {
				override suspend fun createShare(cookbookId: Int) = Err(Failure.ServerError("unused"))

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

		viewModel.importSharedCookbook("share-token")
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookImportCompleted>().single() shouldBe
			AnalyticsEvent.CookbookImportCompleted(
				importedRecipeCount = 2,
				cookbookId = importedCookbook.id,
			)
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookOpened>().single() shouldBe
			AnalyticsEvent.CookbookOpened(
				cookbookId = importedCookbook.id,
				cookbookName = importedCookbook.name,
				recipeCount = importedCookbook.recipeCount,
			)
	}

	@Test
	fun `import shared cookbook tracks failed`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val importUseCase = ImportCookbookShareUseCase(
			object : CookbookShareRepository {
				override suspend fun createShare(cookbookId: Int) = Err(Failure.ServerError("unused"))

				override suspend fun importShare(token: String) = Err(Failure.ServerError("Import failed"))
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
		shareCookbook: ShareCookbookUseCase = unusedShareCookbookUseCase(),
	): FavoritesViewModel = FavoritesViewModel(
		getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
		getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
		createCookbook = CreateCookbookUseCase(cookbooksRepository),
		deleteCookbookUseCase = DeleteCookbookUseCase(cookbooksRepository),
		getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
		getCookbookCoverImageUrl = getCookbookCoverImageUrl,
		importCookbookShare = importCookbookShare,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		shareCookbook = shareCookbook,
		trackEvent = TrackEventUseCase(analyticsRepository),
		sessionKey = "session",
	)
}

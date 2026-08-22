package app.purecipes.feature.library.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveRecipeFromCookbookUseCase
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.CreateCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import app.purecipes.shared.domain.model.CookbookShareToken
import app.purecipes.shared.domain.model.SearchResultsPage
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
class CookbookDetailViewModelAnalyticsTest {

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
	fun `loading cookbook detail tracks cookbook opened`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val cookbooksRepository = FakeCookbooksRepository(
			cookbookRecipesPageResult = Ok(
				SearchResultsPage(
					items = emptyList(),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 4,
				),
			),
		)
		cookbookDetailViewModel(
			cookbooksRepository = cookbooksRepository,
			analyticsRepository = analyticsRepository,
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
	fun `share cookbook tracks cookbook shared`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val shareRepository = object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		}
		val cookbookShareRepository = object : CookbookShareRepository {
			override suspend fun createShare(cookbookId: Int) = Ok(CookbookShareToken(token = "token"))

			override suspend fun importShare(token: String) =
				Err(Failure.ServerError("unused"))
		}
		val viewModel = cookbookDetailViewModel(
			analyticsRepository = analyticsRepository,
			shareCookbook = ShareCookbookUseCase(
				createCookbookShareUseCase = CreateCookbookShareUseCase(cookbookShareRepository),
				shareRepository = shareRepository,
			),
		)
		advanceUntilIdle()
		analyticsRepository.trackedEvents.clear()

		viewModel.shareCookbook()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.CookbookShared>().single() shouldBe
			AnalyticsEvent.CookbookShared(
				cookbookId = 10,
				cookbookName = "Weeknight Dinners",
				origin = AnalyticsOrigin.FAVORITES,
			)
	}

	private fun cookbookDetailViewModel(
		cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(
			cookbookRecipesPageResult = Ok(
				SearchResultsPage(
					items = emptyList(),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 0,
				),
			),
		),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		shareCookbook: ShareCookbookUseCase = unusedShareCookbookUseCase(),
	): CookbookDetailViewModel = CookbookDetailViewModel(
		getCookbookRecipesPage = GetCookbookRecipesPageUseCase(cookbooksRepository),
		removeRecipeFromCookbookUseCase = RemoveRecipeFromCookbookUseCase(cookbooksRepository),
		getCookbookCoverImageUrl = getCookbookCoverImageUrl,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		shareCookbook = shareCookbook,
		trackEvent = TrackEventUseCase(analyticsRepository),
		cookbookId = 10,
		initialName = "Weeknight Dinners",
		sessionKey = "session",
	)
}

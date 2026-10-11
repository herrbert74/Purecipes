package app.purecipes.feature.home.ui

import app.purecipes.feature.home.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.home.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.shared.data.readiness.SearchReadinessCoordinator
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

	@Test
	fun `home shelf loads the next page when requested`() = runViewModelTest {
		val firstPage = List(HOME_SHELF_PAGE_SIZE) { index ->
			RecipeSummary(
				id = index + 1,
				title = "Ready ${index + 1}",
				cuisine = null,
				imageUrl = null,
				totalTime = 20,
			)
		}
		val extra = RecipeSummary(
			id = HOME_SHELF_PAGE_SIZE + 1,
			title = "Ready ${HOME_SHELF_PAGE_SIZE + 1}",
			cuisine = null,
			imageUrl = null,
			totalTime = 20,
		)
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(
				HomeFeed(
					shelves = listOf(
						HomeShelf(
							id = HomeShelfId.NEW,
							title = "New",
							recipes = firstPage,
						),
					),
				),
			),
		)
		homeFeedRepository.shelfPages = mapOf(
			HomeShelfId.NEW to Ok(
				SearchResultsPage(
					items = listOf(extra),
					pageNumber = 2,
					pageSize = HOME_SHELF_PAGE_SIZE,
					totalMatches = HOME_SHELF_PAGE_SIZE + 1,
				),
			),
		)
		val viewModel = homeViewModel(homeFeedRepository)

		advanceUntilIdle()

		val state = viewModel.shelfPagination.getValue(HomeShelfId.NEW)
		state.allItems.size shouldBe HOME_SHELF_PAGE_SIZE
		homeFeedRepository.shelfPageCalls shouldBe emptyList()

		state.requestPage(
			initialPageKey = 1,
			requestedPageKey = 2,
			items = state.allItems,
		)
		advanceUntilIdle()

		state.allItems.size shouldBe HOME_SHELF_PAGE_SIZE + 1
		state.allItems.last().title shouldBe extra.title
		homeFeedRepository.shelfPageCalls shouldBe listOf(HomeShelfId.NEW)
	}

	@Test
	fun `closing feature requests refreshes the home vote and suggest card`() = runViewModelTest {
		val recipe = RecipeSummary(
			id = 1,
			title = "Tomato Pasta",
			cuisine = null,
			imageUrl = null,
			totalTime = 20,
		)
		val voted = featureRequest(title = "Meal plan", voteCount = 1)
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(
				HomeFeed(
					shelves = listOf(
						HomeShelf(
							id = HomeShelfId.NEW,
							title = "New",
							recipes = listOf(recipe),
						),
					),
					featureRequest = voted,
				),
			),
		)
		val homeFeedRefresh = HomeFeedRefreshCoordinator()
		val viewModel = homeViewModel(
			homeFeedRepository = homeFeedRepository,
			homeFeedRefresh = homeFeedRefresh,
		)
		advanceUntilIdle()

		homeFeedRepository.result = Ok(HomeFeed(featureRequest = null))
		homeFeedRefresh.markStale()
		advanceUntilIdle()

		viewModel.homeFeed.featureRequest shouldBe null
		viewModel.homeFeed.shelves.single().recipes.single().title shouldBe recipe.title
		viewModel.shelfPagination.getValue(HomeShelfId.NEW).allItems.single().title shouldBe recipe.title

		val suggested = featureRequest(title = "Weekly plan", voteCount = 4, votedByCurrentUser = true)
		homeFeedRepository.result = Ok(HomeFeed(featureRequest = suggested))
		homeFeedRefresh.markStale()
		advanceUntilIdle()

		viewModel.homeFeed.featureRequest shouldBe suggested
		viewModel.shelfPagination.getValue(HomeShelfId.NEW).allItems.single().title shouldBe recipe.title
	}

	private fun homeViewModel(
		homeFeedRepository: FakeHomeFeedRepository,
		homeFeedRefresh: HomeFeedRefreshCoordinator = HomeFeedRefreshCoordinator(),
	) = HomeViewModel(
		getHomeFeed = GetHomeFeedUseCase(homeFeedRepository),
		getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
		homeFeedRefresh = homeFeedRefresh,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		searchReadiness = SearchReadinessCoordinator(),
		sessionKey = null,
	)

	private fun featureRequest(
		title: String,
		voteCount: Int,
		votedByCurrentUser: Boolean = false,
	) = FeatureRequest(
		id = 4,
		title = title,
		description = "Plan dinners ahead",
		status = FeatureRequestStatus.OPEN,
		voteCount = voteCount,
		commentCount = 0,
		createdAtEpochMillis = 0L,
		votedByCurrentUser = votedByCurrentUser,
	)
}

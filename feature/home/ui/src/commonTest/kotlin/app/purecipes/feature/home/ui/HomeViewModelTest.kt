package app.purecipes.feature.home.ui

import app.purecipes.feature.home.domain.readiness.HomeFeedRefreshCoordinator
import app.purecipes.feature.home.domain.usecase.GetHomeFeedUseCase
import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.recipedetails.domain.usecase.ObserveRecipeViewsUseCase
import app.purecipes.shared.data.readiness.SearchReadinessCoordinator
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeCollectionSource
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeActivityEvent
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.domain.model.shelfKey
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeActivityRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

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

		val state = viewModel.shelfPagination.getValue(HomeShelfId.NEW.name)
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
	fun `collection shelf loads the next page by slug`() = runViewModelTest {
		val firstPage = List(HOME_SHELF_PAGE_SIZE) { index ->
			RecipeSummary(
				id = index + 1,
				title = "Week ${index + 1}",
				cuisine = null,
				imageUrl = null,
				totalTime = 20,
			)
		}
		val extra = RecipeSummary(
			id = HOME_SHELF_PAGE_SIZE + 1,
			title = "Week ${HOME_SHELF_PAGE_SIZE + 1}",
			cuisine = null,
			imageUrl = null,
			totalTime = 20,
		)
		val shelf = HomeShelf(
			id = HomeShelfId.COLLECTION,
			title = "This week",
			recipes = firstPage,
			collectionSlug = "this-week",
			collectionSource = HomeCollectionSource.EDITORIAL,
		)
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(HomeFeed(shelves = listOf(shelf))),
		)
		homeFeedRepository.collectionPages = mapOf(
			"this-week" to Ok(
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

		val state = viewModel.shelfPagination.getValue(shelf.shelfKey())
		state.requestPage(
			initialPageKey = 1,
			requestedPageKey = 2,
			items = state.allItems,
		)
		advanceUntilIdle()

		state.allItems.last().title shouldBe extra.title
		homeFeedRepository.collectionPageCalls shouldBe listOf("this-week")
		homeFeedRepository.shelfPageCalls shouldBe emptyList()
	}

	@Test
	fun `activity shelf loads the next page for its cuisine`() = runViewModelTest {
		val firstPage = List(HOME_SHELF_PAGE_SIZE) { index ->
			RecipeSummary(
				id = index + 1,
				title = "Mexican ${index + 1}",
				cuisine = null,
				imageUrl = null,
				totalTime = 20,
			)
		}
		val extra = RecipeSummary(
			id = HOME_SHELF_PAGE_SIZE + 1,
			title = "Mexican ${HOME_SHELF_PAGE_SIZE + 1}",
			cuisine = null,
			imageUrl = null,
			totalTime = 20,
		)
		val shelf = HomeShelf(
			id = HomeShelfId.HABIT,
			title = "Mexican",
			recipes = firstPage,
			habitCuisine = "Mexican",
		)
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(HomeFeed(shelves = listOf(shelf))),
		)
		homeFeedRepository.habitShelfPages = mapOf(
			"Mexican" to Ok(
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

		val state = viewModel.shelfPagination.getValue(shelf.shelfKey())
		state.requestPage(
			initialPageKey = 1,
			requestedPageKey = 2,
			items = state.allItems,
		)
		advanceUntilIdle()

		state.allItems.last().title shouldBe extra.title
		homeFeedRepository.shelfPageCalls shouldBe listOf(HomeShelfId.HABIT)
		homeFeedRepository.habitCuisineCalls shouldBe listOf("Mexican")
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
		viewModel.shelfPagination.getValue(HomeShelfId.NEW.name).allItems.single().title shouldBe recipe.title

		val suggested = featureRequest(title = "Weekly plan", voteCount = 4, votedByCurrentUser = true)
		homeFeedRepository.result = Ok(HomeFeed(featureRequest = suggested))
		homeFeedRefresh.markStale()
		advanceUntilIdle()

		viewModel.homeFeed.featureRequest shouldBe suggested
		viewModel.shelfPagination.getValue(HomeShelfId.NEW.name).allItems.single().title shouldBe recipe.title
	}

	@Test
	fun `every recipe details view refreshes recently viewed`() = runViewModelTest {
		val first = recipeSummary(id = 1, title = "First")
		val second = recipeSummary(id = 2, title = "Second")
		val newShelf = HomeShelf(
			id = HomeShelfId.NEW,
			title = "New",
			recipes = listOf(first),
		)
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(HomeFeed(shelves = listOf(newShelf))),
		)
		val activity = FakeRecipeActivityRepository()
		val viewModel = homeViewModel(
			homeFeedRepository = homeFeedRepository,
			recipeActivityRepository = activity,
			sessionKey = "user",
		)
		advanceUntilIdle()

		homeFeedRepository.result = Ok(
			HomeFeed(
				shelves = listOf(
					newShelf,
					recentlyViewedShelf(second),
				),
			),
		)
		activity.record(second.id, RecipeActivityEvent.OPEN)
		advanceUntilIdle()

		recentlyViewedTitles(viewModel) shouldBe listOf("Second")

		homeFeedRepository.result = Ok(
			HomeFeed(
				shelves = listOf(
					newShelf,
					recentlyViewedShelf(first, second),
				),
			),
		)
		activity.record(first.id, RecipeActivityEvent.OPEN)
		advanceUntilIdle()

		recentlyViewedTitles(viewModel) shouldBe listOf("First", "Second")
	}

	@Test
	fun `removing a favorite drops it from the favorites shelf`() = runViewModelTest {
		val kept = recipeSummary(id = 1, title = "Kept")
		val removed = recipeSummary(id = 2, title = "Removed")
		val favorites = FakeFavoritesRepository()
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(
				HomeFeed(
					shelves = listOf(
						HomeShelf(
							id = HomeShelfId.FAVORITES,
							title = "Your favorites",
							recipes = listOf(kept, removed),
						),
					),
				),
			),
		)
		val viewModel = homeViewModel(
			homeFeedRepository = homeFeedRepository,
			favoritesRepository = favorites,
			sessionKey = "user",
		)
		advanceUntilIdle()

		homeFeedRepository.result = Ok(
			HomeFeed(
				shelves = listOf(
					HomeShelf(
						id = HomeShelfId.FAVORITES,
						title = "Your favorites",
						recipes = listOf(kept),
					),
				),
			),
		)
		favorites.emitFavoriteEvent(FavoriteEvent.Removed(removed.id))
		advanceUntilIdle()

		viewModel.shelfPagination.getValue(HomeShelfId.FAVORITES.name).allItems.map { recipe ->
			recipe.title
		} shouldBe listOf("Kept")
	}

	@Test
	fun `removing the last favorite hides the favorites shelf`() = runViewModelTest {
		val removed = recipeSummary(id = 2, title = "Removed")
		val favorites = FakeFavoritesRepository()
		val homeFeedRepository = FakeHomeFeedRepository(
			result = Ok(
				HomeFeed(
					shelves = listOf(
						HomeShelf(
							id = HomeShelfId.NEW,
							title = "New",
							recipes = listOf(recipeSummary(id = 1, title = "New pasta")),
						),
						HomeShelf(
							id = HomeShelfId.FAVORITES,
							title = "Your favorites",
							recipes = listOf(removed),
						),
					),
				),
			),
		)
		val viewModel = homeViewModel(
			homeFeedRepository = homeFeedRepository,
			favoritesRepository = favorites,
			sessionKey = "user",
		)
		advanceUntilIdle()

		homeFeedRepository.result = Ok(
			HomeFeed(
				shelves = listOf(
					HomeShelf(
						id = HomeShelfId.NEW,
						title = "New",
						recipes = listOf(recipeSummary(id = 1, title = "New pasta")),
					),
				),
			),
		)
		favorites.emitFavoriteEvent(FavoriteEvent.Removed(removed.id))
		advanceUntilIdle()

		viewModel.homeFeed.shelves.map { shelf -> shelf.id } shouldBe listOf(HomeShelfId.NEW)
		viewModel.shelfPagination.containsKey(HomeShelfId.FAVORITES.name) shouldBe false
	}

	@Test
	fun `initial load keeps the skeleton visible until the splash has cleared`() = runViewModelTest {
		val readiness = SearchReadinessCoordinator()
		val viewModel = homeViewModel(
			homeFeedRepository = FakeHomeFeedRepository(
				result = Ok(
					HomeFeed(
						shelves = listOf(
							HomeShelf(
								id = HomeShelfId.NEW,
								title = "New",
								recipes = listOf(recipeSummary(id = 1, title = "Pasta")),
							),
						),
					),
				),
			),
			searchReadiness = readiness,
		)

		runCurrent()

		viewModel.isHomeLoading shouldBe true
		viewModel.homeFeed.shelves.isEmpty() shouldBe true
		readiness.isReady.value shouldBe true

		advanceTimeBy((HOME_SKELETON_REVEAL_MILLIS - 1).milliseconds)
		runCurrent()

		viewModel.isHomeLoading shouldBe true
		viewModel.homeFeed.shelves.isEmpty() shouldBe true

		advanceTimeBy(1.milliseconds)
		runCurrent()

		viewModel.isHomeLoading shouldBe false
		viewModel.homeFeed.shelves.map { shelf -> shelf.title } shouldBe listOf("New")
	}

	@Test
	fun `home list scroll position is kept for the next visit`() = runViewModelTest {
		val viewModel = homeViewModel(FakeHomeFeedRepository())
		advanceUntilIdle()

		viewModel.onHomeListScrolled(firstVisibleItemIndex = 4, firstVisibleItemScrollOffset = 12)

		viewModel.homeListFirstVisibleItemIndex shouldBe 4
		viewModel.homeListFirstVisibleItemScrollOffset shouldBe 12
	}

	private fun homeViewModel(
		homeFeedRepository: FakeHomeFeedRepository,
		homeFeedRefresh: HomeFeedRefreshCoordinator = HomeFeedRefreshCoordinator(),
		favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
		recipeActivityRepository: FakeRecipeActivityRepository = FakeRecipeActivityRepository(),
		sessionKey: String? = null,
		searchReadiness: SearchReadinessCoordinator = SearchReadinessCoordinator(),
	) = HomeViewModel(
		getHomeFeed = GetHomeFeedUseCase(homeFeedRepository),
		getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
		homeFeedRefresh = homeFeedRefresh,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
		observeRecipeViews = ObserveRecipeViewsUseCase(recipeActivityRepository),
		searchReadiness = searchReadiness,
		sessionKey = sessionKey,
	)

	private fun recentlyViewedShelf(vararg recipes: RecipeSummary) = HomeShelf(
		id = HomeShelfId.RECENTLY_VIEWED,
		title = "Recently viewed",
		recipes = recipes.toList(),
	)

	private fun recentlyViewedTitles(viewModel: HomeViewModel): List<String> {
		return viewModel.homeFeed.shelves
			.first { shelf -> shelf.id == HomeShelfId.RECENTLY_VIEWED }
			.recipes
			.map { recipe -> recipe.title }
	}

	private fun recipeSummary(
		id: Int,
		title: String,
	) = RecipeSummary(
		id = id,
		title = title,
		cuisine = null,
		imageUrl = null,
		totalTime = 20,
		isFavorite = true,
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

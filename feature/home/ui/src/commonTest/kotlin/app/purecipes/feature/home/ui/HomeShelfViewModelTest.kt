package app.purecipes.feature.home.ui

import app.purecipes.feature.home.domain.usecase.GetHomeShelfPageUseCase
import app.purecipes.feature.library.domain.model.FavoriteEvent
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchResultsPage
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeHomeFeedRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeShelfViewModelTest {

	@Test
	fun `a collection shelf loads that list`() = runViewModelTest {
		val recipe = recipeSummary(id = 7, title = "Sunday roast")
		val homeFeedRepository = FakeHomeFeedRepository()
		homeFeedRepository.collectionPages = mapOf(
			"this-week" to Ok(page(recipe)),
		)
		val viewModel = homeShelfViewModel(
			homeFeedRepository = homeFeedRepository,
			shelfId = HomeShelfId.COLLECTION,
			collectionSlug = "this-week",
			title = "This week",
		)

		advanceUntilIdle()

		viewModel.title shouldBe "This week"
		viewModel.recipes.map { item -> item.title } shouldBe listOf("Sunday roast")
		viewModel.totalMatches shouldBe 1
		homeFeedRepository.collectionPageCalls shouldBe listOf("this-week")
		homeFeedRepository.shelfPageCalls.shouldBeEmpty()
	}

	@Test
	fun `an automatic shelf loads the shelf page`() = runViewModelTest {
		val recipe = recipeSummary(id = 3, title = "New pasta")
		val homeFeedRepository = FakeHomeFeedRepository()
		homeFeedRepository.shelfPages = mapOf(
			HomeShelfId.NEW to Ok(page(recipe)),
		)
		val viewModel = homeShelfViewModel(
			homeFeedRepository = homeFeedRepository,
			shelfId = HomeShelfId.NEW,
			title = "New",
		)

		advanceUntilIdle()

		viewModel.recipes.map { item -> item.title } shouldBe listOf("New pasta")
		homeFeedRepository.shelfPageCalls shouldBe listOf(HomeShelfId.NEW)
		homeFeedRepository.collectionPageCalls.shouldBeEmpty()
	}

	@Test
	fun `the next page is appended`() = runViewModelTest {
		val recipe = recipeSummary(id = 3, title = "New pasta")
		val homeFeedRepository = FakeHomeFeedRepository()
		homeFeedRepository.shelfPages = mapOf(
			HomeShelfId.NEW to Ok(
				SearchResultsPage(
					items = listOf(recipe),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 21,
				),
			),
		)
		val viewModel = homeShelfViewModel(
			homeFeedRepository = homeFeedRepository,
			shelfId = HomeShelfId.NEW,
			title = "New",
		)
		advanceUntilIdle()

		viewModel.paginationState.requestPage(
			initialPageKey = 1,
			requestedPageKey = 2,
			items = viewModel.recipes.toList(),
		)
		advanceUntilIdle()

		viewModel.recipes.map { item -> item.title } shouldBe listOf("New pasta", "New pasta")
		homeFeedRepository.shelfPageCalls shouldBe listOf(HomeShelfId.NEW, HomeShelfId.NEW)
	}

	@Test
	fun `a favorite change reloads the shelf`() = runViewModelTest {
		val recipe = recipeSummary(id = 4, title = "Kept pasta", isFavorite = true)
		val homeFeedRepository = FakeHomeFeedRepository()
		homeFeedRepository.shelfPages = mapOf(
			HomeShelfId.FAVORITES to Ok(page(recipe)),
		)
		val favorites = FakeFavoritesRepository()
		val viewModel = HomeShelfViewModel(
			getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(favorites),
			shelfId = HomeShelfId.FAVORITES,
			collectionSlug = null,
			habitCuisine = null,
			title = "Your favorites",
			sessionKey = "user",
		)
		advanceUntilIdle()
		homeFeedRepository.shelfPages = mapOf(
			HomeShelfId.FAVORITES to Ok(
				SearchResultsPage(
					items = emptyList(),
					pageNumber = 1,
					pageSize = 20,
					totalMatches = 0,
				),
			),
		)

		favorites.emitFavoriteEvent(FavoriteEvent.Removed(recipe.id))
		advanceUntilIdle()

		viewModel.recipes.shouldBeEmpty()
		homeFeedRepository.shelfPageCalls shouldBe listOf(HomeShelfId.FAVORITES, HomeShelfId.FAVORITES)
	}

	private fun homeShelfViewModel(
		homeFeedRepository: FakeHomeFeedRepository,
		shelfId: HomeShelfId,
		title: String,
		collectionSlug: String? = null,
	) = HomeShelfViewModel(
		getHomeShelfPage = GetHomeShelfPageUseCase(homeFeedRepository),
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
		shelfId = shelfId,
		collectionSlug = collectionSlug,
		habitCuisine = null,
		title = title,
		sessionKey = null,
	)

	private fun page(recipe: RecipeSummary) = SearchResultsPage(
		items = listOf(recipe),
		pageNumber = 1,
		pageSize = 20,
		totalMatches = 1,
	)

	private fun recipeSummary(
		id: Int,
		title: String,
		isFavorite: Boolean = false,
	) = RecipeSummary(
		id = id,
		title = title,
		cuisine = null,
		imageUrl = null,
		totalTime = 20,
		isFavorite = isFavorite,
	)
}

package app.purecipes.feature.home.data.repository

import app.purecipes.feature.home.data.datasource.HomeFeedRemoteDataSource
import app.purecipes.shared.datatestfixtures.fake.FakePurecipesApi
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class HomeFeedAccessorTest {

	@Test
	fun `returns the home feed from the API`() = runTest {
		val expected = HomeFeed(
			shelves = listOf(HomeShelf(id = HomeShelfId.QUICK, title = "Quick")),
		)
		val api = FakePurecipesApi()
		api.homeFeed = expected
		val accessor = HomeFeedAccessor(HomeFeedRemoteDataSource(api))

		accessor.getHomeFeed().get() shouldBe expected
		api.getHomeCalls shouldBe 1
	}

	@Test
	fun `returns a shelf page from the API`() = runTest {
		val page = SearchResultsPage(
			items = emptyList(),
			pageNumber = 2,
			pageSize = 12,
			totalMatches = 13,
		)
		val api = FakePurecipesApi()
		api.homeShelfPages = mapOf(HomeShelfId.NEW to page)
		val accessor = HomeFeedAccessor(HomeFeedRemoteDataSource(api))

		accessor.getShelfPage(
			shelfId = HomeShelfId.NEW,
			pageNumber = 2,
			pageSize = 12,
			habitCuisine = null,
		).get() shouldBe page
		api.getHomeShelfPageCalls shouldBe listOf("NEW")
		api.getHomeShelfHabitCuisines shouldBe listOf(null)
	}

	@Test
	fun `passes the habit cuisine when loading that shelf page`() = runTest {
		val api = FakePurecipesApi()
		val accessor = HomeFeedAccessor(HomeFeedRemoteDataSource(api))

		accessor.getShelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 2,
			pageSize = 12,
			habitCuisine = "Mexican",
		)
		api.getHomeShelfPageCalls shouldBe listOf("HABIT")
		api.getHomeShelfHabitCuisines shouldBe listOf("Mexican")
	}

	@Test
	fun `returns a collection page from the API`() = runTest {
		val page = SearchResultsPage(
			items = emptyList(),
			pageNumber = 2,
			pageSize = 12,
			totalMatches = 13,
		)
		val api = FakePurecipesApi()
		api.homeCollectionPages = mapOf("this-week" to page)
		val accessor = HomeFeedAccessor(HomeFeedRemoteDataSource(api))

		accessor.getCollectionPage("this-week", pageNumber = 2, pageSize = 12).get() shouldBe page
		api.getHomeCollectionPageCalls shouldBe listOf("this-week")
	}
}

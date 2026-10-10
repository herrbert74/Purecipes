package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.HomeFeedRemoteDataSource
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

		accessor.getShelfPage(HomeShelfId.NEW, pageNumber = 2, pageSize = 12).get() shouldBe page
		api.getHomeShelfPageCalls shouldBe listOf("NEW")
	}
}

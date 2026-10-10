package app.purecipes.feature.home.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelf
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetHomeFeedUseCaseTest {

	@Test
	fun `returns the repository feed`() = runTest {
		val feed = HomeFeed(
			shelves = listOf(
				HomeShelf(id = HomeShelfId.NEW, title = "New"),
			),
		)
		val repository = FakeHomeFeedRepository(Ok(feed))
		val useCase = GetHomeFeedUseCase(repository)

		useCase().get() shouldBe feed
		repository.calls shouldBe 1
	}

	private class FakeHomeFeedRepository(
		private val result: Outcome<HomeFeed>,
	) : HomeFeedRepository {

		var calls: Int = 0

		override suspend fun getHomeFeed(): Outcome<HomeFeed> {
			calls += 1
			return result
		}

		override suspend fun getShelfPage(
			shelfId: HomeShelfId,
			pageNumber: Int,
			pageSize: Int,
		): Outcome<SearchResultsPage> = Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)
	}
}

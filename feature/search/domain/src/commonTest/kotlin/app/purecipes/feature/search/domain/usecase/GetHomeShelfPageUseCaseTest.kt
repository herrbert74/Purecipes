package app.purecipes.feature.search.domain.usecase

import app.purecipes.feature.search.domain.repository.HomeFeedRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetHomeShelfPageUseCaseTest {

	@Test
	fun `returns the repository shelf page`() = runTest {
		val page = SearchResultsPage(
			items = emptyList(),
			pageNumber = 2,
			pageSize = 12,
			totalMatches = 13,
		)
		val repository = FakeShelfRepository(Ok(page))
		val useCase = GetHomeShelfPageUseCase(repository)

		useCase(HomeShelfId.NEW, pageNumber = 2, pageSize = 12).get() shouldBe page
		repository.calls shouldBe 1
	}

	private class FakeShelfRepository(
		private val page: SearchOutcome<SearchResultsPage>,
	) : HomeFeedRepository {

		var calls: Int = 0

		override suspend fun getHomeFeed(): SearchOutcome<HomeFeed> = Ok(HomeFeed())

		override suspend fun getShelfPage(
			shelfId: HomeShelfId,
			pageNumber: Int,
			pageSize: Int,
		): SearchOutcome<SearchResultsPage> {
			calls += 1
			return page
		}
	}
}

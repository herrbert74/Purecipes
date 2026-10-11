package app.purecipes.feature.home.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
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
		repository.shelfCalls shouldBe 1
		repository.collectionCalls shouldBe 0
	}

	@Test
	fun `returns the collection page when a slug is set`() = runTest {
		val page = SearchResultsPage(
			items = emptyList(),
			pageNumber = 2,
			pageSize = 12,
			totalMatches = 13,
		)
		val repository = FakeShelfRepository(Ok(page))
		val useCase = GetHomeShelfPageUseCase(repository)

		useCase(
			HomeShelfId.COLLECTION,
			pageNumber = 2,
			pageSize = 12,
			collectionSlug = "this-week",
		).get() shouldBe page
		repository.shelfCalls shouldBe 0
		repository.collectionCalls shouldBe 1
	}

	private class FakeShelfRepository(
		private val page: Outcome<SearchResultsPage>,
	) : HomeFeedRepository {

		var shelfCalls: Int = 0

		var collectionCalls: Int = 0

		override suspend fun getHomeFeed(): Outcome<HomeFeed> = Ok(HomeFeed())

		override suspend fun getShelfPage(
			shelfId: HomeShelfId,
			pageNumber: Int,
			pageSize: Int,
			ignored: String?,
		): Outcome<SearchResultsPage> {
			shelfCalls += 1
			return page
		}

		override suspend fun getCollectionPage(
			slug: String,
			pageNumber: Int,
			pageSize: Int,
		): Outcome<SearchResultsPage> {
			collectionCalls += 1
			return page
		}
	}
}

package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.HomeFeedRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import com.github.michaelbull.result.Ok

class FakeHomeFeedRepository(
	var result: SearchOutcome<HomeFeed> = Ok(HomeFeed()),
) : HomeFeedRepository {

	var calls: Int = 0
		private set

	var shelfPages: Map<HomeShelfId, SearchOutcome<SearchResultsPage>> = emptyMap()

	val shelfPageCalls = mutableListOf<HomeShelfId>()

	override suspend fun getHomeFeed(): SearchOutcome<HomeFeed> {
		calls += 1
		return result
	}

	override suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> {
		shelfPageCalls += shelfId
		return shelfPages[shelfId] ?: Ok(
			SearchResultsPage(
				items = emptyList(),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = 0,
			),
		)
	}
}

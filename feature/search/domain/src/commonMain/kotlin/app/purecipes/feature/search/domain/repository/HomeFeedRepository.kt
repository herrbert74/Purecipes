package app.purecipes.feature.search.domain.repository

import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage

interface HomeFeedRepository {

	suspend fun getHomeFeed(): SearchOutcome<HomeFeed>

	suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage>
}

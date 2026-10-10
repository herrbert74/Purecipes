package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage

interface HomeFeedDataSource {

	interface Remote {

		suspend fun getHomeFeed(): SearchOutcome<HomeFeed>

		suspend fun getShelfPage(
			shelfId: HomeShelfId,
			pageNumber: Int,
			pageSize: Int,
		): SearchOutcome<SearchResultsPage>
	}
}

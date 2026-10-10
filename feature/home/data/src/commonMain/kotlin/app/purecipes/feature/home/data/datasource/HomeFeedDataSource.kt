package app.purecipes.feature.home.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage

interface HomeFeedDataSource {

	interface Remote {

		suspend fun getHomeFeed(): Outcome<HomeFeed>

		suspend fun getShelfPage(
			shelfId: HomeShelfId,
			pageNumber: Int,
			pageSize: Int,
		): Outcome<SearchResultsPage>
	}
}

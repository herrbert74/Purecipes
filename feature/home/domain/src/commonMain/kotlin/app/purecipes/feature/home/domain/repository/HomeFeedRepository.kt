package app.purecipes.feature.home.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage

interface HomeFeedRepository {

	suspend fun getHomeFeed(): Outcome<HomeFeed>

	suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage>
}

package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.HomeFeedDataSource
import app.purecipes.feature.search.domain.repository.HomeFeedRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class HomeFeedAccessor(
	private val remoteDataSource: HomeFeedDataSource.Remote,
) : HomeFeedRepository {

	override suspend fun getHomeFeed(): SearchOutcome<HomeFeed> = remoteDataSource.getHomeFeed()

	override suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> = remoteDataSource.getShelfPage(shelfId, pageNumber, pageSize)
}

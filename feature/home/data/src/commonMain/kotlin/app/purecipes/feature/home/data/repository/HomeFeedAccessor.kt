package app.purecipes.feature.home.data.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.home.data.datasource.HomeFeedDataSource
import app.purecipes.feature.home.domain.repository.HomeFeedRepository
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

	override suspend fun getHomeFeed(): Outcome<HomeFeed> = remoteDataSource.getHomeFeed()

	override suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> = remoteDataSource.getShelfPage(shelfId, pageNumber, pageSize)
}

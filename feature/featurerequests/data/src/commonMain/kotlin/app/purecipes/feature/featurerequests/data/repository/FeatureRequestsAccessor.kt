package app.purecipes.feature.featurerequests.data.repository

import app.purecipes.feature.featurerequests.data.datasource.FeatureRequestsDataSource
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class FeatureRequestsAccessor(
	private val remoteDataSource: FeatureRequestsDataSource.Remote,
) : FeatureRequestsRepository {

	override suspend fun getFeatureRequestsPage(
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	) = remoteDataSource.getFeatureRequestsPage(sort, status, pageNumber, pageSize)

	override suspend fun getFeatureRequest(requestId: Int) = remoteDataSource.getFeatureRequest(requestId)

	override suspend fun createFeatureRequest(title: String, description: String) =
		remoteDataSource.createFeatureRequest(title, description)

	override suspend fun toggleFeatureRequestVote(requestId: Int) =
		remoteDataSource.toggleFeatureRequestVote(requestId)

	override suspend fun getFeatureRequestComments(requestId: Int) =
		remoteDataSource.getFeatureRequestComments(requestId)

	override suspend fun addFeatureRequestComment(requestId: Int, body: String) =
		remoteDataSource.addFeatureRequestComment(requestId, body)
}

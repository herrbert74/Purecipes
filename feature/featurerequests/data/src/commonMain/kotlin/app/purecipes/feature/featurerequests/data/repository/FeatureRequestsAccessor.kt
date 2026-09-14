package app.purecipes.feature.featurerequests.data.repository

import app.purecipes.feature.featurerequests.data.datasource.FeatureRequestsDataSource
import app.purecipes.feature.featurerequests.domain.model.FeatureRequestEvent
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import com.github.michaelbull.result.getError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class FeatureRequestsAccessor(
	private val remoteDataSource: FeatureRequestsDataSource.Remote,
) : FeatureRequestsRepository {

	private val featureRequestEvents = MutableSharedFlow<FeatureRequestEvent>(extraBufferCapacity = 1)

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
		remoteDataSource.addFeatureRequestComment(requestId, body).also { outcome ->
			if (outcome.getError() == null) {
				featureRequestEvents.tryEmit(FeatureRequestEvent.CommentAdded(requestId))
			}
		}

	override fun observeFeatureRequestEvents(): Flow<FeatureRequestEvent> = featureRequestEvents.asSharedFlow()
}

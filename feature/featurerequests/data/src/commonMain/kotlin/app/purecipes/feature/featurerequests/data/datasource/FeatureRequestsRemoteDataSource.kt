package app.purecipes.feature.featurerequests.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestCommentCreateRequest
import app.purecipes.shared.domain.model.FeatureRequestCreateRequest
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class FeatureRequestsRemoteDataSource(
	private val api: PurecipesApi,
) : FeatureRequestsDataSource.Remote {

	override suspend fun getFeatureRequestsPage(
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<FeatureRequestListPage> = runCatchingApi {
		api.getFeatureRequests(
			sort = sort.name,
			status = status?.name,
			pageNumber = pageNumber,
			pageSize = pageSize,
		)
	}

	override suspend fun getFeatureRequest(requestId: Int): Outcome<FeatureRequest> = runCatchingApi {
		api.getFeatureRequest(requestId)
	}

	override suspend fun createFeatureRequest(title: String, description: String): Outcome<FeatureRequest> =
		runCatchingApi {
			api.createFeatureRequest(
				FeatureRequestCreateRequest(title = title, description = description),
			)
		}

	override suspend fun toggleFeatureRequestVote(requestId: Int): Outcome<FeatureRequest> = runCatchingApi {
		api.toggleFeatureRequestVote(requestId)
	}

	override suspend fun getFeatureRequestComments(requestId: Int): Outcome<List<FeatureRequestComment>> =
		runCatchingApi {
			api.getFeatureRequestComments(requestId)
		}

	override suspend fun addFeatureRequestComment(
		requestId: Int,
		body: String,
	): Outcome<FeatureRequestComment> = runCatchingApi {
		api.addFeatureRequestComment(
			requestId = requestId,
			request = FeatureRequestCommentCreateRequest(body = body),
		)
	}
}

package app.purecipes.feature.featurerequests.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus

interface FeatureRequestsRepository {

	suspend fun getFeatureRequestsPage(
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<FeatureRequestListPage>

	suspend fun getFeatureRequest(requestId: Int): Outcome<FeatureRequest>

	suspend fun createFeatureRequest(title: String, description: String): Outcome<FeatureRequest>

	suspend fun toggleFeatureRequestVote(requestId: Int): Outcome<FeatureRequest>

	suspend fun getFeatureRequestComments(requestId: Int): Outcome<List<FeatureRequestComment>>

	suspend fun addFeatureRequestComment(requestId: Int, body: String): Outcome<FeatureRequestComment>
}

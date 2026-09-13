package app.purecipes.shared.testfixtures.fake

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.featurerequests.domain.repository.FeatureRequestsRepository
import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestListPage
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok

class FakeFeatureRequestsRepository(
	initialFeatureRequests: List<FeatureRequest> = emptyList(),
	initialComments: List<FeatureRequestComment> = emptyList(),
	var failureMessage: String? = null,
) : FeatureRequestsRepository {

	private val featureRequests = initialFeatureRequests.toMutableList()
	private val comments = initialComments.toMutableList()
	private var nextFeatureRequestId = (initialFeatureRequests.maxOfOrNull { it.id } ?: 0) + 1
	private var nextCommentId = (initialComments.maxOfOrNull { it.id } ?: 0) + 1

	val requestedSorts = mutableListOf<FeatureRequestSort>()
	val requestedStatuses = mutableListOf<FeatureRequestStatus?>()
	val createdTitles = mutableListOf<String>()
	val toggledVoteIds = mutableListOf<Int>()
	val addedCommentBodies = mutableListOf<String>()

	override suspend fun getFeatureRequestsPage(
		sort: FeatureRequestSort,
		status: FeatureRequestStatus?,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<FeatureRequestListPage> {
		requestedSorts += sort
		requestedStatuses += status
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		val filtered = featureRequests.filter { status == null || it.status == status }
		val sorted = when (sort) {
			FeatureRequestSort.NEWEST -> filtered.sortedByDescending { it.createdAtEpochMillis }
			FeatureRequestSort.TOP_VOTES -> filtered.sortedWith(
				compareByDescending<FeatureRequest> { it.voteCount }
					.thenByDescending { it.createdAtEpochMillis },
			)
		}
		return Ok(
			FeatureRequestListPage(
				items = sorted.drop((pageNumber - 1) * pageSize).take(pageSize),
				pageNumber = pageNumber,
				pageSize = pageSize,
				totalMatches = sorted.size,
			),
		)
	}

	override suspend fun getFeatureRequest(requestId: Int): Outcome<FeatureRequest> {
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		val featureRequest = featureRequests.firstOrNull { it.id == requestId }
		return if (featureRequest == null) {
			Err(Failure.ServerError("No feature request found for id $requestId"))
		} else {
			Ok(featureRequest)
		}
	}

	override suspend fun createFeatureRequest(title: String, description: String): Outcome<FeatureRequest> {
		createdTitles += title
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		val created = FeatureRequest(
			id = nextFeatureRequestId++,
			title = title,
			description = description,
			status = FeatureRequestStatus.OPEN,
			voteCount = 0,
			commentCount = 0,
			createdAtEpochMillis = 0L,
			votedByCurrentUser = false,
		)
		featureRequests += created
		return Ok(created)
	}

	override suspend fun toggleFeatureRequestVote(requestId: Int): Outcome<FeatureRequest> {
		toggledVoteIds += requestId
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		val index = featureRequests.indexOfFirst { it.id == requestId }
		return if (index < 0) {
			Err(Failure.ServerError("No feature request found for id $requestId"))
		} else {
			val current = featureRequests[index]
			val updated = current.copy(
				voteCount = if (current.votedByCurrentUser) current.voteCount - 1 else current.voteCount + 1,
				votedByCurrentUser = !current.votedByCurrentUser,
			)
			featureRequests[index] = updated
			Ok(updated)
		}
	}

	override suspend fun getFeatureRequestComments(requestId: Int): Outcome<List<FeatureRequestComment>> {
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		return Ok(comments.filter { it.requestId == requestId })
	}

	override suspend fun addFeatureRequestComment(requestId: Int, body: String): Outcome<FeatureRequestComment> {
		addedCommentBodies += body
		failureMessage?.let { return Err(Failure.ServerError(it)) }
		val comment = FeatureRequestComment(
			id = nextCommentId++,
			requestId = requestId,
			authorDisplayName = "Tester",
			body = body,
			createdAtEpochMillis = 0L,
		)
		comments += comment
		return Ok(comment)
	}
}

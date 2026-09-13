package app.purecipes.shared.testfixtures.fake

import app.purecipes.shared.domain.model.FeatureRequest
import app.purecipes.shared.domain.model.FeatureRequestComment
import app.purecipes.shared.domain.model.FeatureRequestStatus

fun fakeFeatureRequest(
	id: Int = 1,
	title: String = "Shopping list",
	description: String = "Turn ingredients into a shopping list.",
	status: FeatureRequestStatus = FeatureRequestStatus.OPEN,
	voteCount: Int = 0,
	commentCount: Int = 0,
	createdAtEpochMillis: Long = 0L,
	votedByCurrentUser: Boolean = false,
): FeatureRequest = FeatureRequest(
	id = id,
	title = title,
	description = description,
	status = status,
	voteCount = voteCount,
	commentCount = commentCount,
	createdAtEpochMillis = createdAtEpochMillis,
	votedByCurrentUser = votedByCurrentUser,
)

fun fakeFeatureRequestComment(
	id: Int = 1,
	requestId: Int = 1,
	authorDisplayName: String = "Tester",
	body: String = "Great idea.",
	createdAtEpochMillis: Long = 0L,
): FeatureRequestComment = FeatureRequestComment(
	id = id,
	requestId = requestId,
	authorDisplayName = authorDisplayName,
	body = body,
	createdAtEpochMillis = createdAtEpochMillis,
)

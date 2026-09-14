package app.purecipes.backend.feature.featurerequests

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.shared.domain.model.FeatureRequestSort
import app.purecipes.shared.domain.model.FeatureRequestStatus
import app.purecipes.shared.domain.model.featureRequestSortFromRawValue
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

private const val HIGHEST_FEATURE_REQUEST_PAGE_SIZE = 200

private const val DEFAULT_FEATURE_REQUEST_PAGE_SIZE = 20

fun Route.featureRequestRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/feature-requests") {
		get {
			call.respondFeatureRequestsList(sessionService, dbProvider)
		}
		post {
			call.respondFeatureRequestCreate(sessionService, dbProvider)
		}
		route("/{id}") {
			get {
				call.respondFeatureRequestDetail(sessionService, dbProvider)
			}
			post("/vote") {
				call.respondFeatureRequestVoteToggle(sessionService, dbProvider)
			}
			get("/comments") {
				call.respondFeatureRequestComments(sessionService, dbProvider)
			}
			post("/comments") {
				call.respondFeatureRequestCommentCreate(sessionService, dbProvider)
			}
		}
	}
}

private suspend fun ApplicationCall.respondFeatureRequestsList(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val rawStatus = request.queryParameters["status"]?.trim()?.takeIf { it.isNotEmpty() }
	val status = rawStatus?.let { raw ->
		FeatureRequestStatus.entries.firstOrNull { it.name == raw.uppercase() }
	}
	if (rawStatus != null && status == null) {
		respondInvalidRequest("Status must be one of ${FeatureRequestStatus.entries.joinToString()}")
		return
	}
	val pageNumber = request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
	val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()
		?.coerceIn(1, HIGHEST_FEATURE_REQUEST_PAGE_SIZE) ?: DEFAULT_FEATURE_REQUEST_PAGE_SIZE
	val repo = FeatureRequestRepository(dbProvider().dataSource)
	respond(
		repo.listFeatureRequests(
			userId = userId,
			sort = featureRequestSort(),
			status = status,
			pageNumber = pageNumber,
			pageSize = pageSize,
		),
	)
}

private suspend fun ApplicationCall.respondFeatureRequestCreate(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val createRequest = receiveFeatureRequestCreateRequestOrRespond() ?: return
	val validationError = validateFeatureRequest(createRequest)
	if (validationError != null) {
		respondInvalidRequest(validationError)
	} else {
		val repo = FeatureRequestRepository(dbProvider().dataSource)
		respond(
			HttpStatusCode.Created,
			repo.createFeatureRequest(
				userId = userId,
				title = createRequest.title.trim(),
				description = createRequest.description.trim(),
			),
		)
	}
}

private suspend fun ApplicationCall.respondFeatureRequestDetail(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val requestId = featureRequestIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = FeatureRequestRepository(dbProvider().dataSource)
	val featureRequest = repo.getFeatureRequest(userId, requestId)
	if (featureRequest == null) {
		respondFeatureRequestNotFound(requestId)
	} else {
		respond(featureRequest)
	}
}

private suspend fun ApplicationCall.respondFeatureRequestVoteToggle(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val requestId = featureRequestIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = FeatureRequestRepository(dbProvider().dataSource)
	val featureRequest = repo.toggleVote(userId, requestId)
	if (featureRequest == null) {
		respondFeatureRequestNotFound(requestId)
	} else {
		respond(featureRequest)
	}
}

private suspend fun ApplicationCall.respondFeatureRequestComments(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val requestId = featureRequestIdOrRespond() ?: return
	requireAuthenticatedUserId(sessionService) ?: return
	val repo = FeatureRequestRepository(dbProvider().dataSource)
	val comments = repo.listComments(requestId)
	if (comments == null) {
		respondFeatureRequestNotFound(requestId)
	} else {
		respond(comments)
	}
}

private suspend fun ApplicationCall.respondFeatureRequestCommentCreate(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val requestId = featureRequestIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val commentRequest = receiveCommentCreateRequestOrRespond()
	if (commentRequest != null) {
		val validationError = validateFeatureRequestComment(commentRequest)
		if (validationError != null) {
			respondInvalidRequest(validationError)
		} else {
			val repo = FeatureRequestRepository(dbProvider().dataSource)
			val comment = repo.addComment(userId = userId, requestId = requestId, body = commentRequest.body.trim())
			if (comment == null) {
				respondFeatureRequestNotFound(requestId)
			} else {
				respond(HttpStatusCode.Created, comment)
			}
		}
	}
}

private fun ApplicationCall.featureRequestSort(): FeatureRequestSort =
	featureRequestSortFromRawValue(request.queryParameters["sort"])

private suspend fun ApplicationCall.featureRequestIdOrRespond(): Int? {
	val requestId = parameters["id"]?.toIntOrNull()
	if (requestId == null) {
		respondInvalidRequest("Feature request id must be a number")
	}
	return requestId
}

private suspend fun ApplicationCall.respondFeatureRequestNotFound(requestId: Int) {
	respond(
		HttpStatusCode.NotFound,
		ErrorResponse(
			message = "Feature request not found",
			detail = "No feature request found for id: $requestId",
		),
	)
}

package app.purecipes.backend.feature.featurerequests

import app.purecipes.backend.ErrorResponse
import app.purecipes.shared.domain.model.FeatureRequestCommentCreateRequest
import app.purecipes.shared.domain.model.FeatureRequestCreateRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

private const val MAX_FEATURE_REQUEST_TITLE_LENGTH = 200

private const val MAX_FEATURE_REQUEST_DESCRIPTION_LENGTH = 4000

private const val MAX_FEATURE_REQUEST_COMMENT_LENGTH = 2000

internal suspend fun ApplicationCall.respondInvalidRequest(detail: String) {
	respond(
		HttpStatusCode.BadRequest,
		ErrorResponse(
			message = "Invalid request",
			detail = detail,
		),
	)
}

internal suspend fun ApplicationCall.receiveFeatureRequestCreateRequestOrRespond(): FeatureRequestCreateRequest? {
	return try {
		receive<FeatureRequestCreateRequest>()
	} catch (_: ContentConvertException) {
		respondInvalidRequest("Request body must contain a title and a description")
		null
	}
}

internal suspend fun ApplicationCall.receiveCommentCreateRequestOrRespond(): FeatureRequestCommentCreateRequest? {
	return try {
		receive<FeatureRequestCommentCreateRequest>()
	} catch (_: ContentConvertException) {
		respondInvalidRequest("Request body must contain a comment")
		null
	}
}

internal fun validateFeatureRequest(request: FeatureRequestCreateRequest): String? {
	val title = request.title.trim()
	val description = request.description.trim()
	return listOfNotNull(
		"Title is required".takeIf { title.isEmpty() },
		"Title is too long".takeIf { title.length > MAX_FEATURE_REQUEST_TITLE_LENGTH },
		"Description is required".takeIf { description.isEmpty() },
		"Description is too long".takeIf { description.length > MAX_FEATURE_REQUEST_DESCRIPTION_LENGTH },
	).firstOrNull()
}

internal fun validateFeatureRequestComment(request: FeatureRequestCommentCreateRequest): String? {
	val body = request.body.trim()
	return listOfNotNull(
		"Comment is required".takeIf { body.isEmpty() },
		"Comment is too long".takeIf { body.length > MAX_FEATURE_REQUEST_COMMENT_LENGTH },
	).firstOrNull()
}

package app.purecipes.backend.feature.auth

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.response.respond

internal suspend fun ApplicationCall.requireAuthenticatedUserId(sessionService: SessionService): Long? {
	val accessToken = bearerToken()
	if (accessToken == null) {
		respondUnauthorized("Missing bearer token")
		return null
	}
	val session = sessionService.getSession(accessToken)
	val userId = session?.user?.id?.toLongOrNull()
	if (session == null) {
		respondUnauthorized("Session is invalid or expired")
	} else if (userId == null) {
		respondUnauthorized("Session user is invalid")
	}
	return userId
}

internal fun ApplicationCall.optionalAuthenticatedUserId(sessionService: SessionService): Long? {
	val accessToken = bearerToken() ?: return null
	val session = sessionService.getSession(accessToken)
	return session?.user?.id?.toLongOrNull()
}

internal suspend fun ApplicationCall.respondUnauthorized(detail: String) {
	respond(
		HttpStatusCode.Unauthorized,
		ErrorResponse(
			message = "Unauthorized",
			detail = detail,
		),
	)
}

internal fun ApplicationCall.bearerToken(): String? {
	val authorizationHeader = request.header(HttpHeaders.Authorization)?.trim().orEmpty()
	if (!authorizationHeader.startsWith("Bearer ", ignoreCase = true)) {
		return null
	}
	return authorizationHeader.substringAfter(' ').trim().takeIf { it.isNotBlank() }
}

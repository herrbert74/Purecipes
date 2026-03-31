package com.purecipes.backend.routes

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.auth.SessionService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.response.respond

internal suspend fun ApplicationCall.requireAuthenticatedUserId(sessionService: SessionService): Long? {
	val accessToken = bearerToken()
		?: return respondUnauthorized("Missing bearer token").let { null }
	val session = sessionService.getSession(accessToken)
		?: return respondUnauthorized("Session is invalid or expired").let { null }
	return session.user.id.toLongOrNull()
		?: respondUnauthorized("Session user is invalid").let { null }
}

internal fun ApplicationCall.optionalAuthenticatedUserId(sessionService: SessionService): Long? {
	val accessToken = bearerToken() ?: return null
	val session = sessionService.getSession(accessToken) ?: return null
	return session.user.id.toLongOrNull()
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

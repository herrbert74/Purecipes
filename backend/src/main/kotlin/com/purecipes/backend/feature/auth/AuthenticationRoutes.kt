package com.purecipes.backend.feature.auth

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.auth.GoogleIdTokenVerificationResult
import com.purecipes.backend.auth.GoogleIdTokenVerifier
import com.purecipes.backend.auth.SessionService
import com.purecipes.shared.domain.model.GoogleSignInRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authenticationRoutes(
	googleIdTokenVerifier: GoogleIdTokenVerifier,
	sessionService: SessionService,
) {
	route("/auth") {
		post("/google") {
			val request = try {
				call.receive<GoogleSignInRequest>()
			} catch (_: ContentConvertException) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Request body must contain a Google id token",
					)
				)
				return@post
			}

			val idToken = request.idToken.trim()
			if (idToken.isBlank()) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Google id token is required",
					)
				)
				return@post
			}

			when (val result = googleIdTokenVerifier.verify(idToken)) {
				is GoogleIdTokenVerificationResult.Success -> call.respond(
					sessionService.createSession(
						provider = "GOOGLE",
						externalUserId = result.user.id,
						email = result.user.email,
						displayName = result.user.displayName,
						firstName = result.user.firstName,
						familyName = result.user.familyName,
						profileImageUrl = result.user.profileImageUrl,
					),
				)
				is GoogleIdTokenVerificationResult.Invalid -> call.respond(
					HttpStatusCode.Unauthorized,
					ErrorResponse(
						message = "Unauthorized",
						detail = result.detail,
					)
				)
				is GoogleIdTokenVerificationResult.ConfigurationError -> call.respond(
					HttpStatusCode.InternalServerError,
					ErrorResponse(
						message = "Authentication unavailable",
						detail = result.detail,
					)
				)
			}
		}

		get("/session") {
			val accessToken = call.bearerToken()
				?: return@get call.respondUnauthorized("Missing bearer token")
			val session = sessionService.getSession(accessToken)
				?: return@get call.respondUnauthorized("Session is invalid or expired")
			call.respond(session)
		}

		post("/sign-out") {
			val accessToken = call.bearerToken()
				?: return@post call.respondUnauthorized("Missing bearer token")
			if (!sessionService.revokeSession(accessToken)) {
				return@post call.respondUnauthorized("Session is invalid or expired")
			}
			call.respond(HttpStatusCode.NoContent)
		}
	}
}

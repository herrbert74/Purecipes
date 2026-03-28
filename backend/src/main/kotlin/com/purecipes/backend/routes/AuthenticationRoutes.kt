package com.purecipes.backend.routes

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.auth.GoogleIdTokenVerificationResult
import com.purecipes.backend.auth.GoogleIdTokenVerifier
import com.purecipes.shared.domain.model.GoogleSignInRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authenticationRoutes(googleIdTokenVerifier: GoogleIdTokenVerifier) {
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
				is GoogleIdTokenVerificationResult.Success -> call.respond(result.user)
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
	}
}

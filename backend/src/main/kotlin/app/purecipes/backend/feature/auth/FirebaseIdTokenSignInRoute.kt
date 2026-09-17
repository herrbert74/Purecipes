package app.purecipes.backend.feature.auth

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.FirebaseIdTokenVerifier
import app.purecipes.backend.auth.GoogleIdTokenVerificationResult
import app.purecipes.backend.auth.SessionService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal inline fun <reified T : Any> Route.firebaseIdTokenSignInRoute(
	path: String,
	provider: String,
	invalidBodyDetail: String,
	blankTokenDetail: String,
	firebaseIdTokenVerifier: FirebaseIdTokenVerifier,
	sessionService: SessionService,
	crossinline idToken: (T) -> String,
) {
	post(path) {
		val request = try {
			call.receive<T>()
		} catch (_: ContentConvertException) {
			call.respond(
				HttpStatusCode.BadRequest,
				ErrorResponse(
					message = "Invalid request",
					detail = invalidBodyDetail,
				)
			)
			return@post
		}

		val token = idToken(request).trim()
		if (token.isBlank()) {
			call.respond(
				HttpStatusCode.BadRequest,
				ErrorResponse(
					message = "Invalid request",
					detail = blankTokenDetail,
				)
			)
			return@post
		}

		when (val result = firebaseIdTokenVerifier.verify(token)) {
			is GoogleIdTokenVerificationResult.Success -> call.respond(
				sessionService.createSession(
					provider = provider,
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
}

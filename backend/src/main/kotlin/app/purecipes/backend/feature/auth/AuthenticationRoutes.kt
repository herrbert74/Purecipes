package app.purecipes.backend.feature.auth

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.FirebaseIdTokenVerifier
import app.purecipes.backend.auth.GoogleIdTokenVerificationResult
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.shared.domain.model.EmailSignInRequest
import app.purecipes.shared.domain.model.FacebookSignInRequest
import app.purecipes.shared.domain.model.GoogleSignInRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authenticationRoutes(
	firebaseIdTokenVerifier: FirebaseIdTokenVerifier,
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/auth") {
		post("/email") {
			val request = try {
				call.receive<EmailSignInRequest>()
			} catch (_: ContentConvertException) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Request body must contain an id token",
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
						detail = "Id token is required",
					)
				)
				return@post
			}

			when (val result = firebaseIdTokenVerifier.verify(idToken)) {
				is GoogleIdTokenVerificationResult.Success -> call.respond(
					sessionService.createSession(
						provider = "EMAIL",
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

		post("/facebook") {
			val request = try {
				call.receive<FacebookSignInRequest>()
			} catch (_: ContentConvertException) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Request body must contain a Facebook id token",
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
						detail = "Facebook id token is required",
					)
				)
				return@post
			}

			when (val result = firebaseIdTokenVerifier.verify(idToken)) {
				is GoogleIdTokenVerificationResult.Success -> call.respond(
					sessionService.createSession(
						provider = "FACEBOOK",
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

			when (val result = firebaseIdTokenVerifier.verify(idToken)) {
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

		deleteAccountRoute(sessionService, dbProvider)
	}
}

private fun Route.deleteAccountRoute(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	delete("/account") {
		val userId = call.requireAuthenticatedUserId(sessionService) ?: return@delete
		val repository = AccountDeletionRepository(dbProvider().dataSource)
		when (repository.deleteAccount(userId)) {
			is AccountDeletionResult.Deleted -> call.respond(HttpStatusCode.NoContent)
			AccountDeletionResult.AccountNotFound -> call.respond(
				HttpStatusCode.NotFound,
				ErrorResponse(
					message = "Account not found",
					detail = "No account found for the current session",
				)
			)

			AccountDeletionResult.RetainedRecipeOwner -> call.respond(
				HttpStatusCode.Forbidden,
				ErrorResponse(
					message = "Account cannot be deleted",
					detail = "This account owns recipes retained after account deletion",
				)
			)
		}
	}
}

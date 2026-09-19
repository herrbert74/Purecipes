package app.purecipes.backend.feature.auth

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.FirebaseIdTokenVerifier
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.shared.domain.model.AppleSignInRequest
import app.purecipes.shared.domain.model.EmailSignInRequest
import app.purecipes.shared.domain.model.FacebookSignInRequest
import app.purecipes.shared.domain.model.GoogleSignInRequest
import io.ktor.http.HttpStatusCode
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
		firebaseIdTokenSignInRoute(
			path = "/email",
			provider = "EMAIL",
			invalidBodyDetail = "Request body must contain an id token",
			blankTokenDetail = "Id token is required",
			firebaseIdTokenVerifier = firebaseIdTokenVerifier,
			sessionService = sessionService,
			idToken = EmailSignInRequest::idToken,
		)
		firebaseIdTokenSignInRoute(
			path = "/apple",
			provider = "APPLE",
			invalidBodyDetail = "Request body must contain an Apple id token",
			blankTokenDetail = "Apple id token is required",
			firebaseIdTokenVerifier = firebaseIdTokenVerifier,
			sessionService = sessionService,
			idToken = AppleSignInRequest::idToken,
		)
		firebaseIdTokenSignInRoute(
			path = "/facebook",
			provider = "FACEBOOK",
			invalidBodyDetail = "Request body must contain a Facebook id token",
			blankTokenDetail = "Facebook id token is required",
			firebaseIdTokenVerifier = firebaseIdTokenVerifier,
			sessionService = sessionService,
			idToken = FacebookSignInRequest::idToken,
		)
		firebaseIdTokenSignInRoute(
			path = "/google",
			provider = "GOOGLE",
			invalidBodyDetail = "Request body must contain a Google id token",
			blankTokenDetail = "Google id token is required",
			firebaseIdTokenVerifier = firebaseIdTokenVerifier,
			sessionService = sessionService,
			idToken = GoogleSignInRequest::idToken,
		)
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

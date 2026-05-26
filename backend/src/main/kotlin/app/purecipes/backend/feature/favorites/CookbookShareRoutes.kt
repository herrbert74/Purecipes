package app.purecipes.backend.feature.favorites

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.cookbookShareRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/cookbooks") {
		post("/{id}/share") {
			val cookbookId = call.parameters["id"]?.toIntOrNull()
			if (cookbookId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Cookbook id must be a number",
					),
				)
				return@post
			}
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@post
			val repo = CookbookShareRepository(dbProvider().dataSource)
			when (val result = repo.createOrGetShare(userId, cookbookId)) {
				is CookbookShareRepository.CreateShareResult.Created ->
					call.respond(HttpStatusCode.Created, result.share)

				CookbookShareRepository.CreateShareResult.CookbookNotFound ->
					call.respond(
						HttpStatusCode.NotFound,
						ErrorResponse(
							message = "Cookbook not found",
							detail = "No cookbook found for id: $cookbookId",
						),
					)
			}
		}
	}

	route("/cookbook-shares") {
		post("/{token}/import") {
			val token = call.parameters["token"]?.trim().orEmpty()
			if (token.isEmpty()) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Share token is required",
					),
				)
				return@post
			}
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@post
			val repo = CookbookShareRepository(dbProvider().dataSource)
			when (val result = repo.importShare(userId, token)) {
				is CookbookShareRepository.ImportShareResult.Imported ->
					call.respond(HttpStatusCode.OK, result.result)

				CookbookShareRepository.ImportShareResult.ShareNotFound ->
					call.respond(
						HttpStatusCode.NotFound,
						ErrorResponse(
							message = "Share not found",
							detail = "This cookbook share link is invalid or has expired",
						),
					)

				CookbookShareRepository.ImportShareResult.CannotImportOwnCookbook ->
					call.respond(
						HttpStatusCode.BadRequest,
						ErrorResponse(
							message = "Cannot import own cookbook",
							detail = "Open this cookbook from your favorites instead",
						),
					)

				CookbookShareRepository.ImportShareResult.ImportFailed ->
					call.respond(
						HttpStatusCode.Conflict,
						ErrorResponse(
							message = "Import failed",
							detail = "Could not create a cookbook with an available name",
						),
					)
			}
		}
	}
}

package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.shared.domain.model.RecipeActivityRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond

internal suspend fun ApplicationCall.respondRecordRecipeActivity(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val recipeId = parameters["id"]?.toIntOrNull()
	if (recipeId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Recipe id must be a number",
			),
		)
		return
	}
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	recordVisibleRecipeActivity(
		dbProvider = dbProvider,
		userId = userId,
		recipeId = recipeId,
	)
}

private suspend fun ApplicationCall.recordVisibleRecipeActivity(
	dbProvider: () -> Db,
	userId: Long,
	recipeId: Int,
) {
	val request = receiveRecipeActivityRequestOrRespond() ?: return
	val recorded = RecipeActivityRepository(dbProvider().dataSource).record(
		userId = userId,
		recipeId = recipeId,
		event = request.event,
	)
	if (!recorded) {
		respond(
			HttpStatusCode.NotFound,
			ErrorResponse(
				message = "Recipe not found",
				detail = "No recipe found for id: $recipeId",
			),
		)
		return
	}
	respond(HttpStatusCode.NoContent)
}

private suspend fun ApplicationCall.receiveRecipeActivityRequestOrRespond(): RecipeActivityRequest? {
	return try {
		receive<RecipeActivityRequest>()
	} catch (_: BadRequestException) {
		respondInvalidRecipeActivity()
		null
	} catch (_: ContentConvertException) {
		respondInvalidRecipeActivity()
		null
	}
}

private suspend fun ApplicationCall.respondInvalidRecipeActivity() {
	respond(
		HttpStatusCode.BadRequest,
		ErrorResponse(
			message = "Invalid request",
			detail = "Request body must contain a recipe activity event",
		),
	)
}

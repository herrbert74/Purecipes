package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.shared.domain.model.RecipeWriteRequest
import app.purecipes.shared.domain.model.SearchRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

internal suspend fun ApplicationCall.receiveRecipeWriteRequestOrRespond(): RecipeWriteRequest? {
	return try {
		receive<RecipeWriteRequest>()
	} catch (_: ContentConvertException) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Request body must contain a recipe payload",
			),
		)
		null
	}
}

internal suspend fun ApplicationCall.receiveSearchRequestOrRespond(): SearchRequest? {
	return try {
		receive<SearchRequest>()
	} catch (_: ContentConvertException) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Request body must contain a search request",
			),
		)
		null
	}
}

internal fun validateRecipeWriteRequest(request: RecipeWriteRequest): String? {
	return listOfNotNull(
		"Recipe title is required".takeIf { request.title.isBlank() },
		"Recipe description is required".takeIf { request.description.isBlank() },
		"At least one cooking step is required".takeIf {
			request.steps.map(String::trim).none(String::isNotEmpty)
		},
		"Ingredient groups contain no ingredients".takeIf {
			request.ingredientGroups.all { group ->
				group.ingredients.all { ingredient -> ingredient.text.isBlank() }
			}
		},
		"Total time must be zero or greater".takeIf {
			val totalTime = request.totalTime
			totalTime != null && totalTime < 0
		},
	).firstOrNull()
}

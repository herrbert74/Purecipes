package app.purecipes.backend.feature.ingredient

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.ingredientRoutes(
	sessionService: SessionService,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	route("/ingredients") {
		get("/match") {
			val name = call.request.queryParameters["name"]?.trim().orEmpty()
			if (name.isEmpty()) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Missing query parameter: name",
					),
				)
				return@get
			}

			call.requireAuthenticatedUserId(sessionService) ?: return@get

			val repository = IngredientMatchRepository(ingredientMatchCorpusCache)
			call.respond(repository.matchIngredient(name))
		}
	}
}

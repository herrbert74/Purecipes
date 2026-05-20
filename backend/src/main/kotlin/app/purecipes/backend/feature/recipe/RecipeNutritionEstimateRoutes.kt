package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.backend.feature.nutrition.RecipeNutritionEstimator
import app.purecipes.shared.domain.model.RecipeNutritionEstimateRequest
import app.purecipes.shared.domain.model.RecipeNutritionEstimateResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.recipeNutritionEstimateRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	post("/nutrition-estimate") {
		if (call.requireAuthenticatedUserId(sessionService) == null) {
			return@post
		}
		val request = call.receiveRecipeNutritionEstimateRequestOrRespond() ?: return@post
		val estimator = RecipeNutritionEstimator(dbProvider().dataSource)
		val summary = estimator.estimate(request.ingredients)
		call.respond(RecipeNutritionEstimateResponse(nutrition = summary))
	}
}

private suspend fun ApplicationCall.receiveRecipeNutritionEstimateRequestOrRespond(): RecipeNutritionEstimateRequest? {
	return try {
		receive<RecipeNutritionEstimateRequest>()
	} catch (_: ContentConvertException) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Request body must contain ingredient lines",
			),
		)
		null
	}
}

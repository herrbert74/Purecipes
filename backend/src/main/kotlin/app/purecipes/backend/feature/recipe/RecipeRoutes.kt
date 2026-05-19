package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.optionalAuthenticatedUserId
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.backend.feature.favorites.CookbookRepository
import app.purecipes.backend.feature.search.SearchRecipeRepository
import app.purecipes.shared.domain.model.RecipeWriteRequest
import app.purecipes.shared.domain.model.SearchRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

private const val HIGHEST_RESULT_COUNT_LIMIT = 200

private const val DEFAULT_RESULT_COUNT_LIMIT = 20

fun Route.recipeRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/recipes") {
		get("/mine") {
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@get
			val repo = RecipeRepository(dbProvider().dataSource)
			call.respond(repo.getRecipesCreatedByUser(userId))
		}

		recipeNutritionEstimateRoutes(sessionService, dbProvider)

		post {
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@post
			val request = call.receiveRecipeWriteRequestOrRespond() ?: return@post
			val validationError = validateRecipeWriteRequest(request)
			if (validationError != null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = validationError,
					),
				)
				return@post
			}

			val repo = RecipeRepository(dbProvider().dataSource)
			call.respond(HttpStatusCode.Created, repo.createRecipe(userId, request))
		}

		get("/search") {
			val query = call.request.queryParameters["query"]?.trim().orEmpty()
			if (query.isEmpty()) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Missing query parameter: query"
					)
				)
				return@get
			}
			val pageNumber = call.request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
			val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull()
				?.coerceIn(1, HIGHEST_RESULT_COUNT_LIMIT) ?: DEFAULT_RESULT_COUNT_LIMIT
			val repo = SearchRecipeRepository(dbProvider().dataSource)
			call.respond(repo.searchByKeywordPaginated(query, pageNumber, pageSize))
		}

		post("/search") {
			val searchRequest = call.receiveSearchRequestOrRespond() ?: return@post
			val repo = SearchRecipeRepository(dbProvider().dataSource)
			call.respond(
				repo.searchWithFilters(
					request = searchRequest,
					userId = call.optionalAuthenticatedUserId(sessionService),
				),
			)
		}

		get("/{id}/cookbooks") {
			val recipeId = call.parameters["id"]?.toIntOrNull()
			if (recipeId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Recipe id must be a number",
					),
				)
				return@get
			}

			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@get
			val repo = CookbookRepository(dbProvider().dataSource)
			call.respond(repo.listRecipeCookbooks(userId, recipeId))
		}

		get("/{id}") {
			val recipeId = call.parameters["id"]?.toIntOrNull()
			if (recipeId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Recipe id must be a number"
					)
				)
				return@get
			}

			val repo = RecipeRepository(dbProvider().dataSource)
			val recipe = repo.getRecipeDetails(
				recipeId = recipeId,
				userId = call.optionalAuthenticatedUserId(sessionService),
			)
			if (recipe == null) {
				call.respond(
					HttpStatusCode.NotFound,
					ErrorResponse(
						message = "Recipe not found",
						detail = "No recipe found for id: $recipeId"
					)
				)
				return@get
			}

			call.respond(recipe)
		}

		put("/{id}") {
			val recipeId = call.parameters["id"]?.toIntOrNull()
			if (recipeId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Recipe id must be a number"
					)
				)
				return@put
			}

			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@put
			val request = call.receiveRecipeWriteRequestOrRespond() ?: return@put
			val validationError = validateRecipeWriteRequest(request)
			if (validationError != null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = validationError,
					),
				)
				return@put
			}

			val repo = RecipeRepository(dbProvider().dataSource)
			val recipe = repo.updateRecipe(userId = userId, recipeId = recipeId, request = request)
			if (recipe == null) {
				call.respond(
					HttpStatusCode.NotFound,
					ErrorResponse(
						message = "Recipe not found",
						detail = "No editable recipe found for id: $recipeId"
					)
				)
				return@put
			}

			call.respond(recipe)
		}
	}
}

private suspend fun ApplicationCall.receiveRecipeWriteRequestOrRespond(): RecipeWriteRequest? {
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

private suspend fun ApplicationCall.receiveSearchRequestOrRespond(): SearchRequest? {
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

private fun validateRecipeWriteRequest(request: RecipeWriteRequest): String? {
	return listOfNotNull(
		"Recipe title is required".takeIf { request.title.isBlank() },
		"Recipe description is required".takeIf { request.description.isBlank() },
		"At least one cooking step is required".takeIf {
			request.steps.map(String::trim).none(String::isNotEmpty)
		},
		"Ingredient groups contain no ingredients".takeIf {
			request.ingredientGroups.any { group ->
				group.ingredients.map(String::trim).none(String::isNotEmpty)
			}
		},
		"Total time must be zero or greater".takeIf {
			val totalTime = request.totalTime
			totalTime != null && totalTime < 0
		},
	).firstOrNull()
}

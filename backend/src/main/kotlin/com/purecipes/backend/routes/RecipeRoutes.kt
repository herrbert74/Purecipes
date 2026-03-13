package com.purecipes.backend.routes

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.db.Db
import com.purecipes.backend.repository.RecipeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

private const val HIGHEST_RESULT_COUNT_LIMIT = 200

private const val DEFAULT_RESULT_COUNT_LIMIT = 50

fun Route.recipeRoutes(dbProvider: () -> Db) {
	route("/recipes") {
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
			val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, HIGHEST_RESULT_COUNT_LIMIT)
				?: DEFAULT_RESULT_COUNT_LIMIT
			val repo = RecipeRepository(dbProvider().dataSource)
			call.respond(repo.searchByKeyword(query, limit))
		}
	}
}

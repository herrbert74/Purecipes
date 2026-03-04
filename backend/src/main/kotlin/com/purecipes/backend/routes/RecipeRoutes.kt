package com.purecipes.backend.routes

import com.purecipes.backend.db.Db
import com.purecipes.backend.repository.RecipeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.recipeRoutes(db: Db) {
	val repo = RecipeRepository(db.dataSource)

	route("/recipes") {
		get("/search") {
			val query = call.request.queryParameters["query"]?.trim().orEmpty()
			if (query.isEmpty()) {
				call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing query parameter: query"))
				return@get
			}
			val limit = call.request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, 200) ?: 50
			call.respond(repo.searchByKeyword(query, limit))
		}
	}
}

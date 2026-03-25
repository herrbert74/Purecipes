package com.purecipes.backend.routes

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.db.Db
import com.purecipes.backend.repository.RecipeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.favoriteRoutes(dbProvider: () -> Db) {
	route("/favorites") {
		get {
			val repo = RecipeRepository(dbProvider().dataSource)
			call.respond(repo.getFavoriteRecipes())
		}

		post("/{id}") {
			val recipeId = call.parameters["id"]?.toIntOrNull()
			if (recipeId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Recipe id must be a number"
					)
				)
				return@post
			}

			val repo = RecipeRepository(dbProvider().dataSource)
			if (!repo.addFavorite(recipeId)) {
				call.respond(
					HttpStatusCode.NotFound,
					ErrorResponse(
						message = "Recipe not found",
						detail = "No recipe found for id: $recipeId"
					)
				)
				return@post
			}

			call.respond(HttpStatusCode.NoContent)
		}

		delete("/{id}") {
			val recipeId = call.parameters["id"]?.toIntOrNull()
			if (recipeId == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Recipe id must be a number"
					)
				)
				return@delete
			}

			val repo = RecipeRepository(dbProvider().dataSource)
			if (!repo.removeFavorite(recipeId)) {
				call.respond(
					HttpStatusCode.NotFound,
					ErrorResponse(
						message = "Recipe not found",
						detail = "No recipe found for id: $recipeId"
					)
				)
				return@delete
			}

			call.respond(HttpStatusCode.NoContent)
		}
	}
}

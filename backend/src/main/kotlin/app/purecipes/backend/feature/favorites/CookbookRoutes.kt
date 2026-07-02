package app.purecipes.backend.feature.favorites

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.shared.domain.model.CookbookCreateRequest
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

private const val HIGHEST_COOKBOOK_PAGE_SIZE = 200

private const val DEFAULT_COOKBOOK_PAGE_SIZE = 20

private const val MAX_COOKBOOK_NAME_LENGTH = 200

fun Route.cookbookRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/cookbooks") {
		get {
			call.respondCookbooksList(sessionService, dbProvider)
		}
		post {
			call.respondCookbookCreate(sessionService, dbProvider)
		}
		route("/{id}") {
			delete {
				call.respondCookbookDelete(sessionService, dbProvider)
			}
			get("/recipes") {
				call.respondCookbookRecipes(sessionService, dbProvider)
			}
			put("/recipes/{recipeId}") {
				call.respondCookbookRecipePut(sessionService, dbProvider)
			}
			delete("/recipes/{recipeId}") {
				call.respondCookbookRecipeDelete(sessionService, dbProvider)
			}
		}
	}
}

private suspend fun ApplicationCall.respondCookbooksList(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val pageNumber = request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
	val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()
		?.coerceIn(1, HIGHEST_COOKBOOK_PAGE_SIZE) ?: DEFAULT_COOKBOOK_PAGE_SIZE
	val repo = CookbookRepository(dbProvider().dataSource)
	respond(repo.listCookbooks(userId, pageNumber, pageSize))
}

private suspend fun ApplicationCall.respondCookbookCreate(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val request = receiveCookbookCreateRequestOrRespond() ?: return
	val validationError = validateCookbookName(request.name)
	if (validationError != null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = validationError,
			),
		)
	} else {
		val repo = CookbookRepository(dbProvider().dataSource)
		when (val result = repo.createCookbook(userId, request.name.trim())) {
			is CookbookRepository.CreateCookbookResult.Created ->
				respond(HttpStatusCode.Created, result.cookbook)

			CookbookRepository.CreateCookbookResult.DuplicateName ->
				respond(
					HttpStatusCode.Conflict,
					ErrorResponse(
						message = "Cookbook already exists",
						detail = "Use a different cookbook name",
					),
				)
		}
	}
}

private suspend fun ApplicationCall.respondCookbookDelete(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val cookbookId = parameters["id"]?.toIntOrNull()
	if (cookbookId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Cookbook id must be a number",
			),
		)
		return
	}
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = CookbookRepository(dbProvider().dataSource)
	if (!repo.deleteCookbook(userId, cookbookId)) {
		respond(
			HttpStatusCode.NotFound,
			ErrorResponse(
				message = "Cookbook not found",
				detail = "No cookbook found for id: $cookbookId",
			),
		)
	} else {
		respond(HttpStatusCode.NoContent)
	}
}

private suspend fun ApplicationCall.respondCookbookRecipes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val cookbookId = parameters["id"]?.toIntOrNull()
	if (cookbookId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Cookbook id must be a number",
			),
		)
		return
	}
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val pageNumber = request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
	val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()
		?.coerceIn(1, HIGHEST_COOKBOOK_PAGE_SIZE) ?: DEFAULT_COOKBOOK_PAGE_SIZE
	val repo = CookbookRepository(dbProvider().dataSource)
	val page = repo.listCookbookRecipes(userId, cookbookId, pageNumber, pageSize)
	if (page == null) {
		respond(
			HttpStatusCode.NotFound,
			ErrorResponse(
				message = "Cookbook not found",
				detail = "No cookbook found for id: $cookbookId",
			),
		)
	} else {
		respond(page)
	}
}

private suspend fun ApplicationCall.respondCookbookRecipePut(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val cookbookId = parameters["id"]?.toIntOrNull()
	val recipeId = parameters["recipeId"]?.toIntOrNull()
	if (cookbookId == null || recipeId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Cookbook id and recipe id must be numbers",
			),
		)
		return
	}
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = CookbookRepository(dbProvider().dataSource)
	when (repo.addRecipeToCookbook(userId, cookbookId, recipeId)) {
		CookbookRepository.AddRecipeToCookbookResult.Added ->
			respond(HttpStatusCode.NoContent)

		CookbookRepository.AddRecipeToCookbookResult.NotFavorite ->
			respond(
				HttpStatusCode.BadRequest,
				ErrorResponse(
					message = "Recipe not saved",
					detail = "Add the recipe to favorites before adding it to a cookbook",
				),
			)

		CookbookRepository.AddRecipeToCookbookResult.CookbookNotFound ->
			respond(
				HttpStatusCode.NotFound,
				ErrorResponse(
					message = "Cookbook not found",
					detail = "No cookbook found for id: $cookbookId",
				),
			)

		CookbookRepository.AddRecipeToCookbookResult.RecipeNotFound ->
			respond(
				HttpStatusCode.NotFound,
				ErrorResponse(
					message = "Recipe not found",
					detail = "No recipe found for id: $recipeId",
				),
			)
	}
}

private suspend fun ApplicationCall.respondCookbookRecipeDelete(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val cookbookId = parameters["id"]?.toIntOrNull()
	val recipeId = parameters["recipeId"]?.toIntOrNull()
	if (cookbookId == null || recipeId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Cookbook id and recipe id must be numbers",
			),
		)
		return
	}
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = CookbookRepository(dbProvider().dataSource)
	if (!repo.removeRecipeFromCookbook(userId, cookbookId, recipeId)) {
		respond(
			HttpStatusCode.NotFound,
			ErrorResponse(
				message = "Not found",
				detail = "Cookbook or recipe membership was not found",
			),
		)
	} else {
		respond(HttpStatusCode.NoContent)
	}
}

private suspend fun ApplicationCall.receiveCookbookCreateRequestOrRespond(): CookbookCreateRequest? {
	return try {
		receive<CookbookCreateRequest>()
	} catch (_: ContentConvertException) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Request body must contain a cookbook name",
			),
		)
		null
	}
}

private fun validateCookbookName(raw: String): String? {
	val trimmed = raw.trim()
	return listOfNotNull(
		"Cookbook name is required".takeIf { trimmed.isEmpty() },
		"Cookbook name is too long".takeIf { trimmed.length > MAX_COOKBOOK_NAME_LENGTH },
	).firstOrNull()
}

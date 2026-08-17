package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.optionalAuthenticatedUserId
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.backend.feature.ingredient.IngredientMatchCorpusCache
import app.purecipes.backend.feature.library.CookbookRepository
import app.purecipes.backend.feature.search.SearchRecipeRepository
import app.purecipes.backend.feature.subscription.UserPremiumRepository
import app.purecipes.shared.domain.model.RecipeWriteRequest
import app.purecipes.shared.domain.model.canPrivatizeRecipe
import app.purecipes.shared.domain.model.canUseKeyIngredients
import app.purecipes.shared.domain.model.canUsePremiumFilters
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

private const val HIGHEST_RESULT_COUNT_LIMIT = 200

private const val DEFAULT_RESULT_COUNT_LIMIT = 20

internal suspend fun ApplicationCall.respondMyRecipes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = RecipeRepository(dbProvider().dataSource)
	respond(repo.getRecipesCreatedByUser(userId))
}

internal suspend fun ApplicationCall.respondCreateRecipe(
	sessionService: SessionService,
	dbProvider: () -> Db,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val request = receiveRecipeWriteRequestOrRespond()
	val validationError = request?.let(::validateRecipeWriteRequest)
	if (request != null && validationError == null) {
		if (!canSetRecipePrivate(dbProvider, userId, request.isPrivate, currentlyPrivate = false)) {
			respondPrivateRecipePremiumRequired()
			return
		}
		val repo = RecipeRepository(dbProvider().dataSource)
		respond(HttpStatusCode.Created, repo.createRecipe(userId, request))
		ingredientMatchCorpusCache.invalidate()
	} else if (request != null && validationError != null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = validationError,
			),
		)
	}
}

internal suspend fun ApplicationCall.respondKeywordSearch(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val query = request.queryParameters["query"]?.trim().orEmpty()
	if (query.isEmpty()) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Missing query parameter: query"
			)
		)
		return
	}
	val pageNumber = request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
	val pageSize = request.queryParameters["pageSize"]?.toIntOrNull()
		?.coerceIn(1, HIGHEST_RESULT_COUNT_LIMIT) ?: DEFAULT_RESULT_COUNT_LIMIT
	val repo = SearchRecipeRepository(dbProvider().dataSource)
	respond(
		repo.searchByKeywordPaginated(
			keyword = query,
			pageNumber = pageNumber,
			pageSize = pageSize,
			userId = optionalAuthenticatedUserId(sessionService),
		),
	)
}

internal suspend fun ApplicationCall.respondFilteredSearch(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val request = receiveSearchRequestOrRespond() ?: return
	val dataSource = dbProvider().dataSource
	val userId = optionalAuthenticatedUserId(sessionService)
	val isPremium = userId?.let { UserPremiumRepository(dataSource).isPremium(it) } ?: false
	// Temporary: premium search features stay ungated until RevenueCat/Google Play
	// premium sync is in place (see TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM).
	val effectiveRequest = request.copy(
		filters = if (canUsePremiumFilters(isPremium)) {
			request.filters
		} else {
			request.filters.withoutPremiumFilters()
		},
		keyIngredients = if (canUseKeyIngredients(isPremium)) {
			request.keyIngredients
		} else {
			emptySet()
		},
	)
	val repo = SearchRecipeRepository(dataSource)
	respond(repo.searchWithFilters(effectiveRequest, userId))
}

internal suspend fun ApplicationCall.respondRecipeCookbooks(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val recipeId = requireRecipeIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = CookbookRepository(dbProvider().dataSource)
	respond(repo.listRecipeCookbooks(userId, recipeId))
}

internal suspend fun ApplicationCall.respondRecipeDetails(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val recipeId = requireRecipeIdOrRespond() ?: return
	val repo = RecipeRepository(dbProvider().dataSource)
	val recipe = repo.getRecipeDetails(
		recipeId = recipeId,
		userId = optionalAuthenticatedUserId(sessionService),
	)
	if (recipe == null) {
		respond(
			HttpStatusCode.NotFound,
			ErrorResponse(
				message = "Recipe not found",
				detail = "No recipe found for id: $recipeId"
			)
		)
		return
	}

	respond(recipe)
}

internal suspend fun ApplicationCall.respondUpdateRecipe(
	sessionService: SessionService,
	dbProvider: () -> Db,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	val recipeId = requireRecipeIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val request = receiveRecipeWriteRequestOrRespond()
	val validationError = request?.let(::validateRecipeWriteRequest)
	if (request != null && validationError == null) {
		respondValidatedRecipeUpdate(
			recipeId = recipeId,
			userId = userId,
			request = request,
			dbProvider = dbProvider,
			ingredientMatchCorpusCache = ingredientMatchCorpusCache,
		)
	} else if (request != null && validationError != null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = validationError,
			),
		)
	}
}

internal suspend fun ApplicationCall.respondDeleteRecipe(
	sessionService: SessionService,
	dbProvider: () -> Db,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	val recipeId = requireRecipeIdOrRespond() ?: return
	val userId = requireAuthenticatedUserId(sessionService) ?: return
	val repo = RecipeRepository(dbProvider().dataSource)
	if (!repo.deleteCreatedRecipe(userId = userId, recipeId = recipeId)) {
		respondEditableRecipeNotFound(recipeId)
	} else {
		respond(HttpStatusCode.NoContent)
		ingredientMatchCorpusCache.invalidate()
	}
}

private suspend fun ApplicationCall.requireRecipeIdOrRespond(): Int? {
	val recipeId = parameters["id"]?.toIntOrNull()
	if (recipeId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Recipe id must be a number",
			),
		)
		return null
	}
	return recipeId
}

private suspend fun ApplicationCall.respondValidatedRecipeUpdate(
	recipeId: Int,
	userId: Long,
	request: RecipeWriteRequest,
	dbProvider: () -> Db,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	val repo = RecipeRepository(dbProvider().dataSource)
	when {
		!repo.isRecipeOwnedByUser(userId = userId, recipeId = recipeId) -> {
			respondEditableRecipeNotFound(recipeId)
		}

		!canSetRecipePrivate(
			dbProvider,
			userId,
			request.isPrivate,
			currentlyPrivate = repo.isRecipePrivate(recipeId) == true,
		) -> {
			respondPrivateRecipePremiumRequired()
		}

		else -> {
			val recipe = repo.updateRecipe(userId = userId, recipeId = recipeId, request = request)
			if (recipe == null) {
				respondEditableRecipeNotFound(recipeId)
			} else {
				respond(recipe)
				ingredientMatchCorpusCache.invalidate()
			}
		}
	}
}

private suspend fun ApplicationCall.respondEditableRecipeNotFound(recipeId: Int) {
	respond(
		HttpStatusCode.NotFound,
		ErrorResponse(
			message = "Recipe not found",
			detail = "No editable recipe found for id: $recipeId",
		),
	)
}

private fun canSetRecipePrivate(
	dbProvider: () -> Db,
	userId: Long,
	requestedPrivate: Boolean,
	currentlyPrivate: Boolean,
): Boolean {
	if (!requestedPrivate || currentlyPrivate) {
		return true
	}
	return canPrivatizeRecipe(UserPremiumRepository(dbProvider().dataSource).isPremium(userId))
}

private suspend fun ApplicationCall.respondPrivateRecipePremiumRequired() {
	respond(
		HttpStatusCode.Forbidden,
		ErrorResponse(
			message = "Premium required",
			detail = "Private recipes are available to premium members",
		),
	)
}

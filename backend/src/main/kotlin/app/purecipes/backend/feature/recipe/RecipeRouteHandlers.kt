package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.optionalAuthenticatedUserId
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.backend.feature.favorites.CookbookRepository
import app.purecipes.backend.feature.ingredient.IngredientMatchCorpusCache
import app.purecipes.backend.feature.search.SearchRecipeRepository
import app.purecipes.backend.feature.subscription.UserPremiumRepository
import app.purecipes.shared.domain.model.canUseKeyIngredients
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

internal suspend fun ApplicationCall.respondKeywordSearch(dbProvider: () -> Db) {
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
	respond(repo.searchByKeywordPaginated(query, pageNumber, pageSize))
}

internal suspend fun ApplicationCall.respondFilteredSearch(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val request = receiveSearchRequestOrRespond() ?: return
	val dataSource = dbProvider().dataSource
	val userId = optionalAuthenticatedUserId(sessionService)
	val isPremium = userId?.let { UserPremiumRepository(dataSource).isPremium(it) } ?: false
	// Temporary: key ingredients stay ungated until RevenueCat/Google Play premium sync
	// is in place (see TREAT_KEY_INGREDIENTS_AS_NON_PREMIUM). Other premium filters still gate.
	val effectiveRequest = request.copy(
		filters = if (isPremium) request.filters else request.filters.withoutPremiumFilters(),
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
		val repo = RecipeRepository(dbProvider().dataSource)
		val recipe = repo.updateRecipe(userId = userId, recipeId = recipeId, request = request)
		if (recipe == null) {
			respond(
				HttpStatusCode.NotFound,
				ErrorResponse(
					message = "Recipe not found",
					detail = "No editable recipe found for id: $recipeId"
				)
			)
		} else {
			respond(recipe)
			ingredientMatchCorpusCache.invalidate()
		}
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
